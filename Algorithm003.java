import java.util.*;

class Algorithm003 extends Interface {
    Algorithm003(boolean alphaSide, boolean isEnemySecret) {
        super(alphaSide, isEnemySecret);
    }

    private double attackProbability;

    public void SetParameter(int[] parameters) {
        attackProbability = parameters[0] / 100;
    }

    public void Think() {
        if (IsEnemyLastAttack()) {
            if (EnemyLastAttackResult().contains(Board.ATTACK_SINK)) {
                allySumHp--;
                allyCount--;
            }
            if (EnemyLastAttackResult().contains(Board.ATTACK_HIT)) {
                allySumHp--;
            }
        }
        if (IsAllyLastAttack()) {
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_SINK)) {
                enemySumHp--;
                enemyCount--;
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_HIT)) {
                enemySumHp--;
            }
        }
        Board.SearchEnableAttackPoints(alphaSide);

        // ここから
        if (Board.IsLastMove(!alphaSide)) {
            Board.NormalizeValues(alphaSide, 0);
        }
        if (IsEnemyLastAttack()) {
            // 敵に攻撃された
            Board.GetCell(EnemyLastAttackPoint()).SetValue(alphaSide, 0, 0);
            for (Point point : Board.GetRoundPoints(EnemyLastAttackPoint())) {
                Board.GetCell(point).SetValue(alphaSide, 0, Board.GetCell(point).GetValue(alphaSide, 0) + 1);
            }
            if (EnemyLastAttackResult().contains(Board.ATTACK_SINK)) {
                // 敵に撃沈された
            }
            if (EnemyLastAttackResult().contains(Board.ATTACK_HIT)) {
                // 敵に命中された
                ArrayList<Point> points = new ArrayList<Point>();
                for (Point point : Board.GetCrossPoints(EnemyLastAttackPoint(), 1, 2)) {
                    if (Board.IsMoveEnablePoint(alphaSide, EnemyLastAttackPoint(),
                            point)) {
                        points.add(point);
                    }
                }
                // 移動できる範囲からランダムに移動
                DoMove(EnemyLastAttackPoint(), Board.GetRandomPoint(points));
                return;
            }
            if (EnemyLastAttackResult().contains(Board.ATTACK_NEAR)) {
                // 敵に波高しされた
            }
        }
        if (IsAllyLastAttack()) {
            // 敵を攻撃した
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_SINK)) {
                // 敵を撃沈した
                Board.GetCell(AllyLastAttackPoint()).SetValue(alphaSide, 0, 0);
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_HIT)) {
                // 敵を命中した
                Board.GetCell(AllyLastAttackPoint()).SetValue(alphaSide, 0, 10);
                if (Board.IsLastMove(!alphaSide)) {
                    // 敵が移動した
                    if (enemyCount == 1) {
                        // 敵が1機のみ
                        Board.GetCell(AllyLastAttackPoint()).SetValue(alphaSide, 0, 0);
                        Board.GetCell(AllyLastAttackPoint()
                                .Plus(EnemyLastMoveVector())).SetValue(alphaSide, 0, 10);
                        if (Board.IsEnableAttackPoint(alphaSide, AllyLastAttackPoint()
                                .Plus(EnemyLastMoveVector()))) {
                            // 攻撃が可能なら攻撃する
                            DoAttack(AllyLastAttackPoint()
                                    .Plus(EnemyLastMoveVector()));
                            return;
                        }
                    } else {
                        // 敵が2機以上
                    }
                } else {
                    // 敵が移動しなかった
                    DoAttack(AllyLastAttackPoint());
                    return;
                }
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NEAR)) {
                // 敵を波高しした
                Board.GetCell(AllyLastAttackPoint()).SetValue(alphaSide, 0, 0);
                for (Point point : Board.GetRoundPoints(AllyLastAttackPoint())) {
                    Board.GetCell(point).SetValue(alphaSide, 0,
                            Board.GetCell(point).GetValue(alphaSide, 0) + 1);
                }
                if (Board.IsLastMove(!alphaSide)) {
                    // 敵が移動した
                } else {
                    // 敵が移動しなかった
                }
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NOHIT)) {
                Board.GetCell(AllyLastAttackPoint()).SetValue(alphaSide, 0, 0);
                for (Point point : Board.GetRoundPoints(AllyLastAttackPoint())) {
                    Board.GetCell(point).SetValue(alphaSide, 0, 0);
                }
            }
        }
        Random random = new Random();
        if (random.nextDouble() <= attackProbability && Board.GetMaxValuePoints(alphaSide, true, 0).size() != 0) {
            DoAttack(Board.GetRandomPoint(Board.GetMaxValuePoints(alphaSide, true, 0)));
            return;
        } else {
            Point oldPoint = Board.GetRandomPoint(Board.GetShipPoints(alphaSide));
            ArrayList<Point> points = new ArrayList<Point>();
            for (Point point : Board.GetCrossPoints(oldPoint, 1, 2)) {
                if (Board.IsMoveEnablePoint(alphaSide, oldPoint, point)) {
                    points.add(point);
                }
            }
            if (points.size() != 0) {
                DoMove(oldPoint, Board.GetRandomPoint(points));
                return;
            } else {
                Board.WriteDisableTurn();
            }
        }
    }
}
