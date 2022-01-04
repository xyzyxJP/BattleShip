import java.util.*;

class Algorithm006 extends Interface {
    Algorithm006(boolean alphaSide, boolean isEnemySecret) {
        super(alphaSide, isEnemySecret);
        Board.InitializeValues(alphaSide, 1);
    }

    private boolean estimatedAttacked = false;
    private Point estimatedBeforePoint = null;
    private boolean prepareTurned = false;
    private Point preparePoint = null;

    public void SetParameter(int[] parameters) {

    }

    public void Think() {
        if (Board.IsLastAttack(!alphaSide)) {
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_SINK)) {
                allySumHp--;
                allyCount--;
                if (allyCount == 0) {
                    Board.Interrupt();
                }
            }
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_HIT)) {
                allySumHp--;
            }
        }
        if (Board.IsLastAttack(alphaSide)) {
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_SINK)) {
                enemySumHp--;
                enemyCount--;
                if (enemyCount == 0) {
                    Board.Interrupt();
                }
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_HIT)) {
                enemySumHp--;
            }
        }
        Board.SearchEnableAttackPoints(alphaSide);

        if (Board.GetLastMoveVector(!alphaSide) != null) {
            switch (Board.GetLastMoveVector(!alphaSide).x) {
                case 2:
                case 1:
                    for (int x = Board.GetLastMoveVector(!alphaSide).x; x < Board.BOARD_SIZE; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE; y++) {
                            Board.GetCell(x, y).SetValue(alphaSide, 0, Board.GetCell(x, y).GetValue(alphaSide, 0) + 1);
                        }
                    }
                    break;
                case -1:
                case -2:
                    for (int x = 0; x < Board.BOARD_SIZE; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE + Board.GetLastMoveVector(!alphaSide).x; y++) {
                            Board.GetCell(x, y).SetValue(alphaSide, 0, Board.GetCell(x, y).GetValue(alphaSide, 0) + 1);
                        }
                    }
                    break;
            }
            switch (Board.GetLastMoveVector(!alphaSide).y) {
                case 2:
                case 1:
                    for (int x = 0; x < Board.BOARD_SIZE; x++) {
                        for (int y = Board.GetLastMoveVector(!alphaSide).y; y < Board.BOARD_SIZE; y++) {
                            Board.GetCell(x, y).SetValue(alphaSide, 0, Board.GetCell(x, y).GetValue(alphaSide, 0) + 1);
                        }
                    }
                    break;
                case -1:
                case -2:
                    for (int x = 0; x < Board.BOARD_SIZE; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE + Board.GetLastMoveVector(!alphaSide).y; y++) {
                            Board.GetCell(x, y).SetValue(alphaSide, 0, Board.GetCell(x, y).GetValue(alphaSide, 0) + 1);
                        }
                    }
                    break;
            }
        }

        if (Board.IsLastAttack(alphaSide)) {
            // 攻撃
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_SINK)) {
                // 撃沈
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValue(alphaSide, 0, -1);
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValue(alphaSide, 1, -1);
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_HIT)) {
                // 命中
                // 被移動
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValue(alphaSide, 0, 10);
                if (Board.IsLastMove(!alphaSide)) {
                    Point estimatedPoint = Board.GetLastAttackPoint(alphaSide)
                            .Plus(Board.GetLastMoveVector(!alphaSide));

                    if (estimatedPoint.IsRange()) {
                        Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValue(alphaSide, 0, 0);
                        Board.GetCell(estimatedPoint).SetValue(alphaSide, 0, 10);
                        if (Board.IsEnableAttackPoint(alphaSide, estimatedPoint)) {
                            estimatedAttacked = true;
                            estimatedBeforePoint = Board.GetLastAttackPoint(alphaSide);
                            DoAttack(estimatedPoint);
                            return;
                        }
                    }
                } else {
                    if (Board.IsEnableAttackPoint(alphaSide, Board.GetLastAttackPoint(alphaSide))) {
                        DoAttack(Board.GetLastAttackPoint(alphaSide));
                        return;
                    }
                }
            } else {
                if (estimatedAttacked) {
                    estimatedAttacked = false;
                    if (Board.IsEnableAttackPoint(alphaSide, estimatedBeforePoint)) {
                        DoAttack(estimatedBeforePoint);
                    }
                    estimatedBeforePoint = null;
                    return;
                }
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NEAR)) {
                // 波高し
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValue(alphaSide, 0, 0);
                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(alphaSide))) {
                    Board.GetCell(point).SetValue(alphaSide, 0,
                            Board.GetCell(point).GetValue(alphaSide, 0) + 1);
                }
                // 被移動
                if (Board.IsLastMove(!alphaSide)) {
                } else {
                }
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NOHIT)) {
                // 外れ
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValue(alphaSide, 0, 0);
                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(alphaSide))) {
                    Board.GetCell(point).SetValue(alphaSide, 0, 0);
                }
            }
        }

        if (Board.IsLastAttack(!alphaSide)) {
            // 被攻撃
            // 自軍評価
            Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValue(alphaSide, 0, 0);
            for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(!alphaSide))) {
                Board.GetCell(point).SetValue(alphaSide, 0, Board.GetCell(point).GetValue(alphaSide, 0) + 1);
            }
            // 敵軍評価
            Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValue(alphaSide, 1, 0);

            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_SINK)) {
                // 被撃沈
                // 自軍評価
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValue(alphaSide, 0, -1);
                // 敵軍評価
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValue(alphaSide, 1, -1);
            }
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_HIT)) {
                // 被命中
                // 敵軍評価
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValue(alphaSide, 1, 10);

                ArrayList<Point> points = new ArrayList<Point>();
                for (Point point : Board.GetCrossPoints(Board.GetLastAttackPoint(!alphaSide), 2, 2)) {
                    if (Board.IsMoveEnablePoint(alphaSide, Board.GetLastAttackPoint(!alphaSide),
                            point)) {
                        points.add(point);
                    }
                }
                if (points.size() != 0) {
                    DoMove(Board.GetLastAttackPoint(!alphaSide), Board.GetRandomPoint(
                            new ArrayList<Point>(Board.GetPointValues(alphaSide, points, 1, -1).keySet())));
                    return;
                }
            }
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_NEAR)) {
                // 被波高し
                // 敵軍評価
                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(!alphaSide))) {
                    Board.GetCell(point).SetValue(alphaSide, 1,
                            Board.GetCell(point).GetValue(alphaSide, 1) + 1);
                }

            }
        }

        if (prepareTurned && Board.IsEnableAttackPoint(alphaSide, preparePoint)) {
            prepareTurned = false;
            DoAttack(preparePoint);
            preparePoint = null;
            return;
        }
        if (Board.GetCell(Board.GetMaxValuePoints(alphaSide, false, 0).get(0)).GetValue(alphaSide, 0) > 5 && Board
                .GetMaxValuePoints(alphaSide, true, 0).size() != 0) {
            if (Board.GetCell(Board.GetMaxValuePoints(alphaSide, false, 0).get(0)).GetValue(alphaSide, 0) != Board
                    .GetCell(Board.GetMaxValuePoints(alphaSide, true, 0).get(0)).GetValue(alphaSide, 0)) {
                preparePoint = Board.GetRandomPoint(Board.GetMaxValuePoints(alphaSide, false, 0));
                if (Board.GetCell(preparePoint).IsAlive(alphaSide)) {
                    if (Board.GetFilterMoveEnablePoints(alphaSide,
                            preparePoint,
                            Board.GetCrossPoints(preparePoint, 1, 2)).size() != 0) {
                        DoMove(preparePoint, Board.GetRandomPoint(Board.GetFilterMoveEnablePoints(alphaSide,
                                preparePoint,
                                Board.GetCrossPoints(preparePoint, 1, 2))));
                        return;
                    }
                } else {
                    Point movePoint = Board.GetRandomPoint(Board.GetShortPoints(alphaSide, preparePoint));
                    Point minusPoint = preparePoint.Minus(movePoint);
                    Point vectorPoint = null;
                    if (minusPoint.x > 1) {
                        vectorPoint = new Point(2, 0);
                    }
                    if (minusPoint.x < -1) {
                        vectorPoint = new Point(-2, 0);
                    }
                    if (minusPoint.y > 1) {
                        vectorPoint = new Point(0, 2);
                    }
                    if (minusPoint.y < -1) {
                        vectorPoint = new Point(0, -2);
                    }
                    if (vectorPoint != null) {
                        if (!Board.IsMoveEnableVector(alphaSide, movePoint, vectorPoint)
                                || minusPoint.x + minusPoint.y < 2) {
                            vectorPoint = vectorPoint.Divide(2);
                            if (!Board.IsMoveEnableVector(alphaSide, movePoint, vectorPoint)) {
                                vectorPoint = null;
                            }
                        }
                    }
                    if (vectorPoint != null) {
                        DoMove(movePoint, movePoint.Plus(vectorPoint));
                        return;
                    }
                }
            }
        }

        if (Board.GetMaxValuePoints(alphaSide, true, 0).size() != 0) {
            DoAttack(Board.GetRandomPoint(Board.GetMaxValuePoints(alphaSide, true, 0)));
            return;
        } else {
            for (Point point : Board.GetShipPoints(alphaSide)) {
                if (Board.GetFilterMoveEnablePoints(alphaSide, point, Board.GetCrossPoints(point, 1, 2)).size() != 0) {
                    DoMove(point, Board.GetRandomPoint(
                            Board.GetFilterMoveEnablePoints(alphaSide, point, Board.GetCrossPoints(point, 1, 2))));
                    return;
                }
            }
        }
        Board.WriteDisableTurn();
    }
}
