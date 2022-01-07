import java.util.*;

class Algorithm015 extends Interface {
    Algorithm015(boolean alphaSide, boolean isEnemySecret) {
        super(alphaSide, isEnemySecret);
        Board.InitializeValues(alphaSide, 1);
    }

    private boolean estimatedAttackedFlag = false;
    private Point estimatedBeforePoint = null;
    private boolean prepareTurned = false;
    private Point preparePoint = null;

    private int enemyMoveCount = 0;

    // private boolean defenceMoveFlag = false;

    public void SetParameter(int[] parameters) {

    }

    public void Think() {
        // #region HP値 推測
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
        // #endregion

        // #region 敵軍移動 推測
        if (Board.GetLastMoveVector(!alphaSide) != null) {
            // 移動先の可能性があるポイントの評価値に1を追加する
            enemyMoveCount++;
            ArrayList<Point> excludePoints = new ArrayList<Point>();
            for (int x = 0; x < Board.BOARD_SIZE; x++) {
                for (int y = 0; y < Board.BOARD_SIZE; y++) {
                    Point point = (new Point(x, y)).Plus(Board.GetLastMoveVector(!alphaSide));
                    if (Board.GetCell(x, y).GetValue(alphaSide, 0) == -1
                            || Board.GetCell(x, y).GetValue(alphaSide, 0) == -2) {
                        if (point.IsRange() && Board.GetCell(point).GetValue(alphaSide, 0) == -1) {
                            excludePoints.add(point);
                        }
                    }
                    if (Board.GetLastMoveVector(!alphaSide).Distance() == 2) {
                        if ((new Point(x, y).Plus(Board.GetLastMoveVector(!alphaSide)).Divide(2)).IsRange()) {
                            if (Board.GetCell(new Point(x, y).Plus(Board.GetLastMoveVector(!alphaSide)).Divide(2))
                                    .GetValue(alphaSide, 0) == -2) {
                                excludePoints.add(point);
                            }
                        }
                    }
                }
            }
            switch (Board.GetLastMoveVector(!alphaSide).x) {
                case 2:
                case 1:
                    for (int x = Board.GetLastMoveVector(!alphaSide).x; x < Board.BOARD_SIZE; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 0);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 0, value + 1);
                        }
                    }
                    break;
                case -1:
                case -2:
                    for (int x = 0; x < Board.BOARD_SIZE + Board.GetLastMoveVector(!alphaSide).x; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 0);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 0, value + 1);
                        }
                    }
                    break;
            }
            switch (Board.GetLastMoveVector(!alphaSide).y) {
                case 2:
                case 1:
                    for (int x = 0; x < Board.BOARD_SIZE; x++) {
                        for (int y = Board.GetLastMoveVector(!alphaSide).y; y < Board.BOARD_SIZE; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 0);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 0, value + 1);
                        }
                    }
                    break;
                case -1:
                case -2:
                    for (int x = 0; x < Board.BOARD_SIZE; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE + Board.GetLastMoveVector(!alphaSide).y; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 0);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 0, value + 1);
                        }
                    }
                    break;
            }
        }
        // #endregion

        // #region 敵軍攻撃 推測
        if (Board.IsLastAttack(!alphaSide)) {
            // 攻撃したポイントの評価値を-1に固定する, 周囲のポイントの評価値に1を追加する
            Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValueForce(alphaSide, 0, -1);
            for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(!alphaSide))) {
                Board.GetCell(point).AddValue(alphaSide, 0, 1);
            }
            // 自軍が撃沈した = 命中したポイントの評価値, 逆評価値を-2に固定する
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_SINK)) {
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValueForce(alphaSide, 0, -2);
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValueForce(alphaSide, 1, -2);
            }
            // 自軍が命中した = 命中したポイントの逆評価値を10に設定する
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_HIT)) {
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValue(alphaSide, 1, 20);
            }
            // 自軍が波高しした = 攻撃したポイントの逆評価値を0に設定する, 周囲のポイントに1を追加する
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_NEAR)) {
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValue(alphaSide, 1, 0);
                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(!alphaSide))) {
                    Board.GetCell(point).AddValue(alphaSide, 1, 1);
                }
            }
            // 自軍が外れした = 攻撃したポイント, 周囲のポイントの逆評価値を-1に固定する
            if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_NOHIT)) {
                Board.GetCell(Board.GetLastAttackPoint(!alphaSide)).SetValueForce(alphaSide, 1, -1);
                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(!alphaSide))) {
                    Board.GetCell(point).SetValueForce(alphaSide, 1, -1);
                }
            }
        }
        // #endregion

        // #region 自軍移動 推測
        if (Board.GetLastMoveVector(alphaSide) != null) {
            // 移動先の可能性があるポイントの逆評価値に1を追加する
            ArrayList<Point> excludePoints = new ArrayList<Point>();
            for (int x = 0; x < Board.BOARD_SIZE; x++) {
                for (int y = 0; y < Board.BOARD_SIZE; y++) {
                    Point point = (new Point(x, y)).Plus(Board.GetLastMoveVector(alphaSide));
                    if (Board.GetCell(x, y).GetValue(alphaSide, 1) == -1
                            || Board.GetCell(x, y).GetValue(alphaSide, 1) == -2) {
                        if (point.IsRange() && Board.GetCell(point).GetValue(alphaSide, 1) == -1) {
                            excludePoints.add(point);
                        }
                    }
                    if (Board.GetLastMoveVector(alphaSide).Distance() == 2) {
                        if ((new Point(x, y).Plus(Board.GetLastMoveVector(alphaSide)).Divide(2)).IsRange()) {
                            if (Board.GetCell(new Point(x, y).Plus(Board.GetLastMoveVector(alphaSide)).Divide(2))
                                    .GetValue(alphaSide, 1) == -2) {
                                excludePoints.add(point);
                            }
                        }
                    }
                }
            }
            switch (Board.GetLastMoveVector(alphaSide).x) {
                case 2:
                case 1:
                    for (int x = Board.GetLastMoveVector(alphaSide).x; x < Board.BOARD_SIZE; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 1);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 1, value + 1);
                        }
                    }
                    break;
                case -1:
                case -2:
                    for (int x = 0; x < Board.BOARD_SIZE + Board.GetLastMoveVector(alphaSide).x; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 1);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 1, value + 1);
                        }
                    }
                    break;
            }
            switch (Board.GetLastMoveVector(alphaSide).y) {
                case 2:
                case 1:
                    for (int x = 0; x < Board.BOARD_SIZE; x++) {
                        for (int y = Board.GetLastMoveVector(alphaSide).y; y < Board.BOARD_SIZE; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 1);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 1, value + 1);
                        }
                    }
                    break;
                case -1:
                case -2:
                    for (int x = 0; x < Board.BOARD_SIZE; x++) {
                        for (int y = 0; y < Board.BOARD_SIZE + Board.GetLastMoveVector(alphaSide).y; y++) {
                            if (excludePoints.contains(new Point(x, y))) {
                                continue;
                            }
                            int value = Board.GetCell(x, y).GetValue(alphaSide, 1);
                            value = value < 0 ? 0 : value;
                            Board.GetCell(x, y).SetValueForce(alphaSide, 1, value + 1);
                        }
                    }
                    break;
            }
        }
        // #endregion

        // #region 自軍攻撃 推測
        if (Board.IsLastAttack(alphaSide)) {
            // 攻撃したポイントの逆評価値を-1に固定する, 周囲のポイントの逆評価値に1を追加する
            Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValueForce(alphaSide, 1, -1);
            for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(alphaSide))) {
                Board.GetCell(point).AddValue(alphaSide, 1, 1);
            }
            // 敵軍が撃沈した = 命中したポイントの評価値, 逆評価値を-2に固定する
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_SINK)) {
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValueForce(alphaSide, 0, -2);
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValueForce(alphaSide, 1, -2);

                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(alphaSide))) {
                    if (Board.GetCell(point).GetValue(alphaSide, 0) > 0) {
                        Board.GetCell(point).AddValue(alphaSide, 0, -1);
                    }
                }
            }
            // 敵軍が命中した = 命中したポイントの評価値を20に設定する, 命中したポイントのX軸Y軸対称のポイントの評価値に5を追加する,
            // = 命中したポイントのX軸, Y軸対称のポイントの評価値に3を追加する
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_HIT)) {
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValueForce(alphaSide, 0, 20);

                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(alphaSide))) {
                    Board.GetCell(point).AddValue(alphaSide, 0, -1);
                }

                if (enemyMoveCount == 0) {
                    Point xySymmetryPoint = new Point(
                            Math.abs(Board.GetLastAttackPoint(alphaSide).x - (Board.BOARD_SIZE - 1)),
                            Math.abs(Board.GetLastAttackPoint(alphaSide).y - (Board.BOARD_SIZE - 1)));
                    Board.GetCell(xySymmetryPoint).AddValue(alphaSide, 0, 5);

                    Point xSymmetryPoint = new Point(
                            Math.abs(Board.GetLastAttackPoint(alphaSide).x - (Board.BOARD_SIZE - 1)),
                            Board.GetLastAttackPoint(alphaSide).y);
                    Board.GetCell(xSymmetryPoint).AddValue(alphaSide, 0, 3);

                    Point ySymmetryPoint = new Point(
                            Math.abs(Board.GetLastAttackPoint(alphaSide).x - (Board.BOARD_SIZE - 1)),
                            Board.GetLastAttackPoint(alphaSide).y);
                    Board.GetCell(ySymmetryPoint).AddValue(alphaSide, 0, 3);
                }

            }
            // 敵軍が波高しした = 攻撃したポイントの評価値を-1に固定する, 周囲のポイントの評価値に1を追加する
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NEAR)) {
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValueForce(alphaSide, 0, -1);
                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(alphaSide))) {
                    Board.GetCell(point).AddValue(alphaSide, 0, 1);
                }
            }
            // 敵軍が外れした = 攻撃したポイント, 周囲のポイントの評価値を-1に固定する
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NOHIT)) {
                Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValueForce(alphaSide, 0, -1);
                for (Point point : Board.GetRoundPoints(Board.GetLastAttackPoint(alphaSide))) {
                    Board.GetCell(point).SetValueForce(alphaSide, 0, -1);
                }
            }
        }
        // #endregion

        // #region 勝ち逃げ 無効
        // if (allySumHp - enemySumHp > 3) {
        // defenceMoveFlag = true;
        // }
        // if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_HIT)
        // || Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_SINK)) {
        // defenceMoveFlag = false;
        // }
        // if (defenceMoveFlag) {
        // Point movePoint = Board.GetRandomPoint(Board.GetShipPoints(alphaSide));
        // ArrayList<Point> points = new ArrayList<Point>();
        // for (Point point : Board.GetCrossPoints(movePoint, 2, 2)) {
        // if (Board.IsMoveEnablePoint(alphaSide, movePoint, point)) {
        // points.add(point);
        // }
        // }
        // if (points.size() != 0) {
        // DoMove(movePoint, Board.GetRandomPoint(points));
        // return;
        // }
        // }
        // #endregion

        // #region 自軍攻撃 攻撃
        if (Board.IsLastAttack(alphaSide)) {
            // 敵軍が命中した
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_HIT)) {
                // 敵軍が移動した = 命中したポイントに移動ベクトルを足したポイントが範囲内ならそのポイントに移動したと判断し、攻撃可能範囲内なら攻撃する (A)
                // 敵軍が移動しなかった = 命中したポイントにもう一度攻撃する
                if (Board.IsLastMove(!alphaSide)) {
                    Point estimatedPoint = Board.GetLastAttackPoint(alphaSide)
                            .Plus(Board.GetLastMoveVector(!alphaSide));
                    if (estimatedPoint.IsRange()) {
                        Board.GetCell(Board.GetLastAttackPoint(alphaSide)).SetValueForce(alphaSide, 0, 0);
                        Board.GetCell(estimatedPoint).SetValueForce(alphaSide, 0, 20);
                        if (Board.IsEnableAttackPoint(alphaSide, estimatedPoint)) {
                            estimatedAttackedFlag = true;
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
                // 敵軍が命中しなかった = (A) の攻撃結果の場合は移動する前のポイントが攻撃可能範囲内なら攻撃する
            } else {
                if (estimatedAttackedFlag) {
                    estimatedAttackedFlag = false;
                    if (Board.IsEnableAttackPoint(alphaSide, estimatedBeforePoint)) {
                        DoAttack(estimatedBeforePoint);
                        estimatedBeforePoint = null;
                        return;
                    }
                }
            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NEAR)) {

            }
            if (Board.GetLastAttackResult(alphaSide).contains(Board.ATTACK_NOHIT)) {

            }
        }
        // #endregion

        // #region 敵軍攻撃 移動 無効
        // if (Board.IsLastAttack(!alphaSide)) {
        // // 自軍が命中した
        // if (Board.GetLastAttackResult(!alphaSide).contains(Board.ATTACK_HIT)) {
        // allyEscapeCount++;
        // Point movePoint;
        // if (allyEscapeCount % 2 != 0 && allyCount != 1) {
        // movePoint = Board.GetRandomPoint(Board.GetShipPoints(alphaSide));
        // while (movePoint.Equal(Board.GetLastAttackPoint(!alphaSide))) {
        // movePoint = Board.GetRandomPoint(Board.GetShipPoints(alphaSide));
        // }
        // } else {
        // movePoint = Board.GetLastAttackPoint(!alphaSide);
        // }
        // ArrayList<Point> points = new ArrayList<Point>();
        // for (Point point : Board.GetCrossPoints(movePoint, 2, 2)) {
        // if (Board.IsMoveEnablePoint(alphaSide, movePoint, point)) {
        // points.add(point);
        // }
        // }
        // if (points.size() != 0) {
        // DoMove(movePoint, Board.GetRandomPoint(points));
        // return;
        // }
        // }
        // }
        // #endregion

        // #region 準備移動 攻撃
        if (prepareTurned && Board.IsEnableAttackPoint(alphaSide, preparePoint)) {
            // System.out.println("Preparing Attack = " + Logger.GetFileName() + " (" +
            // Board.GetTurnCount() + ")");
            prepareTurned = false;
            DoAttack(preparePoint);
            preparePoint = null;
            return;
        }
        // #endregion

        ArrayList<Point> maxValuePoints = new ArrayList<Point>(Board.GetPointValues(alphaSide, null, 0, 1).keySet());
        if (Board.GetCell(maxValuePoints.get(0)).GetValue(alphaSide, 0) > 10) {
            for (Point point : maxValuePoints) {
                if (Board.GetCell(point).IsAlive(alphaSide)) {
                    HashMap<Point, Integer> crossPointValues = new HashMap<Point, Integer>();
                    for (Point crossPoint : Board.GetFilterMoveEnablePoints(alphaSide, point,
                            Board.GetCrossPoints(point, 1, 1))) {
                        crossPointValues.put(crossPoint, Board.GetCell(crossPoint).GetValue(alphaSide, 1));
                    }
                    if (crossPointValues.size() != 0) {
                        int value = Collections.max(crossPointValues.values());
                        for (Map.Entry<Point, Integer> crossPointValue : crossPointValues.entrySet()) {
                            if (crossPointValue.getValue() == value) {
                                prepareTurned = true;
                                preparePoint = point;
                                DoMove(point, crossPointValue.getKey());
                                return;
                            }
                        }
                    }
                } else {
                    for (Point movePoint : Board.GetShortPoints(alphaSide, point)) {
                        Point minusVector = point.Minus(movePoint);
                        Point moveVector = null;
                        if (minusVector.x > 1) {
                            moveVector = new Point(2, 0);
                        } else if (minusVector.x < -1) {
                            moveVector = new Point(-2, 0);
                        } else if (minusVector.y > 1) {
                            moveVector = new Point(0, 2);
                        } else if (minusVector.y < -1) {
                            moveVector = new Point(0, -2);
                        }
                        if (moveVector != null) {
                            if (!Board.IsMoveEnableVector(alphaSide, movePoint, moveVector)
                                    || point.equals(movePoint.Plus(moveVector))) {
                                moveVector = moveVector.Divide(2);
                            }
                            if (Board.IsMoveEnableVector(alphaSide, movePoint, moveVector)) {
                                prepareTurned = true;
                                preparePoint = point;
                                DoMove(movePoint, movePoint.Plus(moveVector));
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (Board.GetMaxValuePoints(alphaSide, true, 0).size() != 0) {
            DoAttack(Board.GetRandomPoint(Board.GetMaxValuePoints(alphaSide, true, 0)));
            return;
        } else {
            Board.WriteDisableTurn();
        }
    }
}