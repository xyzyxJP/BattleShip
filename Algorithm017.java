import java.util.*;

class Algorithm017 extends Interface {
    Algorithm017(boolean alphaSide, boolean isEnemySecret) {
        super(alphaSide, isEnemySecret);
        Board.InitializeValues(alphaSide, 1);
    }

    private int estimatedAttackedStatus = 0;
    private Point estimatedBeforePoint = null;
    private boolean prepareTurned = false;
    private Point preparePoint = null;
    private boolean fakeMoveEnable = true;
    private boolean fakeMoveFlag = false;

    private int enemyMoveCount = 0;

    private int enemyFakeMoveCount = 0;
    private int enemyRealMoveCount = 0;
    private int enemyNoMoveCount = 0;

    private int allyAttackType = 0;

    private final int TYPE_SEARCH = 1;
    private final int TYPE_HIT = 2;
    private final int TYPE_FAKEMOVE = 3;
    private final int TYPE_REALMOVE = 4;
    private final int TYPE_NOMOVE = 5;

    public void SetParameter(int[] parameters) {

    }

    private void MoveValue(int layer, Point vector) {
        ArrayList<Point> excludePoints = new ArrayList<Point>();
        for (int x = 0; x < Board.BOARD_SIZE; x++) {
            for (int y = 0; y < Board.BOARD_SIZE; y++) {
                Point point = (new Point(x, y)).Plus(vector);
                if (Board.GetCell(x, y).GetValue(alphaSide, layer) == -1
                        || Board.GetCell(x, y).GetValue(alphaSide, layer) == -2) {
                    if (point.IsRange() && Board.GetCell(point).GetValue(alphaSide, layer) == -1) {
                        excludePoints.add(point);
                    }
                }
                if (vector.Distance() == 2) {
                    if ((new Point(x, y).Plus(vector.Divide(2))).IsRange()) {
                        if (Board.GetCell(new Point(x, y).Plus(vector.Divide(2)))
                                .GetValue(alphaSide, layer) == -2) {
                            excludePoints.add(point);
                        }
                    }
                }
            }
        }

        switch (vector.x) {
            case 2:
            case 1:
                for (int x = vector.x; x < Board.BOARD_SIZE; x++) {
                    for (int y = 0; y < Board.BOARD_SIZE; y++) {
                        if (excludePoints.contains(new Point(x, y))) {
                            continue;
                        }
                        int value = CalcMoveValue(layer, new Point(x, y), vector);
                        Board.GetCell(x, y).SetValueForce(alphaSide, layer, value);
                    }
                }
                break;
            case -1:
            case -2:
                for (int x = 0; x < Board.BOARD_SIZE + vector.x; x++) {
                    for (int y = 0; y < Board.BOARD_SIZE; y++) {
                        if (excludePoints.contains(new Point(x, y))) {
                            continue;
                        }
                        int value = CalcMoveValue(layer, new Point(x, y), vector);
                        Board.GetCell(x, y).SetValueForce(alphaSide, layer, value);
                    }
                }
                break;
        }
        switch (vector.y) {
            case 2:
            case 1:
                for (int x = 0; x < Board.BOARD_SIZE; x++) {
                    for (int y = vector.y; y < Board.BOARD_SIZE; y++) {
                        if (excludePoints.contains(new Point(x, y))) {
                            continue;
                        }
                        int value = CalcMoveValue(layer, new Point(x, y), vector);
                        Board.GetCell(x, y).SetValueForce(alphaSide, layer, value);
                    }
                }
                break;
            case -1:
            case -2:
                for (int x = 0; x < Board.BOARD_SIZE; x++) {
                    for (int y = 0; y < Board.BOARD_SIZE + vector.y; y++) {
                        if (excludePoints.contains(new Point(x, y))) {
                            continue;
                        }
                        int value = CalcMoveValue(layer, new Point(x, y), vector);
                        Board.GetCell(x, y).SetValueForce(alphaSide, layer, value);
                    }
                }
                break;
        }
    }

    private int CalcMoveValue(int layer, Point point, Point vector) {
        int newValue = Board.GetCell(point).GetValue(alphaSide, layer);
        newValue = newValue < 0 ? 0 : newValue;
        int oldValue = Board.GetCell(point.Minus(vector)).GetValue(alphaSide, layer);
        if (0 <= oldValue && oldValue < 5) {
            newValue += 0;
        } else if (5 <= oldValue && oldValue < 10) {
            newValue += 3;
        } else if (10 <= oldValue && oldValue < 20) {
            newValue += 5;
        } else if (20 <= oldValue) {
            newValue += 10;
        }
        return newValue;
    }

    private void Estimate() {
        if (IsEnemyLastAttack()) {
            if (EnemyLastAttackResult().contains(Board.RESULT_SINK)) {
                allySumHp--;
                allyCount--;
                if (allyCount == 0) {
                    Board.Interrupt();
                }
            }
            if (EnemyLastAttackResult().contains(Board.RESULT_HIT)) {
                allySumHp--;
            }
        }
        if (IsAllyLastAttack()) {
            if (AllyLastAttackResult().contains(Board.RESULT_SINK)) {
                enemySumHp--;
                enemyCount--;
                if (enemyCount == 0) {
                    Board.Interrupt();
                }
            }
            if (AllyLastAttackResult().contains(Board.RESULT_HIT)) {
                enemySumHp--;
            }
        }
        Board.SearchEnableAttackPoints(alphaSide);
    }

    public void Think() {
        Estimate();

        // ????????????????????? = ????????????????????????????????????????????????????????????1???????????????
        if (IsAllyLastMove()) {
            MoveValue(1, AllyLastMoveVector());
        }

        // ????????????????????? = ??????????????????????????????????????????-1???????????????, ???????????????????????????????????????1???????????????
        if (IsAllyLastAttack()) {
            Board.GetCell(AllyLastAttackPoint()).SetValueForce(alphaSide, 1, -1);
            for (Point point : Board.GetRoundPoints(AllyLastAttackPoint())) {
                Board.GetCell(point).AddValue(alphaSide, 1, 1);
            }
            // ????????????????????? = ????????????????????????????????????, ???????????????-2???????????????
            if (AllyLastAttackResult().contains(Board.RESULT_SINK)) {
                Board.GetCell(AllyLastAttackPoint()).SetValueForce(alphaSide, 0, -2);
                Board.GetCell(AllyLastAttackPoint()).SetValueForce(alphaSide, 1, -2);

                for (Point point : Board.GetRoundPoints(AllyLastAttackPoint())) {
                    if (Board.GetCell(point).GetValue(alphaSide, 0) > 0) {
                        Board.GetCell(point).AddValue(alphaSide, 0, -1);
                    }
                }
            }
            // ????????????????????? = ???????????????????????????????????????20???????????????, ???????????????????????????X???Y???????????????????????????????????????5???????????????,
            // = ???????????????????????????X???, Y???????????????????????????????????????3???????????????
            if (AllyLastAttackResult().contains(Board.RESULT_HIT)) {
                Board.GetCell(AllyLastAttackPoint()).SetValueForce(alphaSide, 0, 20);

                for (Point point : Board.GetRoundPoints(AllyLastAttackPoint())) {
                    Board.GetCell(point).AddValue(alphaSide, 0, -1);
                }

                if (enemyMoveCount == 0) {
                    Point xySymmetryPoint = new Point(
                            Math.abs(AllyLastAttackPoint().x - (Board.BOARD_SIZE - 1)),
                            Math.abs(AllyLastAttackPoint().y - (Board.BOARD_SIZE - 1)));
                    Board.GetCell(xySymmetryPoint).AddValue(alphaSide, 0, 5);

                    Point xSymmetryPoint = new Point(
                            Math.abs(AllyLastAttackPoint().x - (Board.BOARD_SIZE - 1)),
                            AllyLastAttackPoint().y);
                    Board.GetCell(xSymmetryPoint).AddValue(alphaSide, 0, 3);

                    Point ySymmetryPoint = new Point(
                            Math.abs(AllyLastAttackPoint().x - (Board.BOARD_SIZE - 1)),
                            AllyLastAttackPoint().y);
                    Board.GetCell(ySymmetryPoint).AddValue(alphaSide, 0, 3);
                }

            }
            // ???????????????????????? = ???????????????????????????????????????-1???????????????, ????????????????????????????????????1???????????????
            if (AllyLastAttackResult().contains(Board.RESULT_NEAR)) {
                Board.GetCell(AllyLastAttackPoint()).SetValueForce(alphaSide, 0, -1);
                for (Point point : Board.GetRoundPoints(AllyLastAttackPoint())) {
                    Board.GetCell(point).AddValue(alphaSide, 0, 1);
                }
            }
            // ????????????????????? = ????????????????????????, ????????????????????????????????????-1???????????????
            if (AllyLastAttackResult().contains(Board.RESULT_NOHIT)) {
                Board.GetCell(AllyLastAttackPoint()).SetValueForce(alphaSide, 0, -1);
                for (Point point : Board.GetRoundPoints(AllyLastAttackPoint())) {
                    Board.GetCell(point).SetValueForce(alphaSide, 0, -1);
                }
            }
            if (AllyLastAttackResult().contains(Board.RESULT_SINK)
                    || AllyLastAttackResult().contains(Board.RESULT_HIT)) {
                switch (allyAttackType) {
                    case TYPE_FAKEMOVE:
                        enemyFakeMoveCount++;
                        break;
                    case TYPE_REALMOVE:
                        enemyRealMoveCount++;
                        break;
                    case TYPE_NOMOVE:
                        enemyNoMoveCount++;
                        break;
                }
            } else {
                switch (allyAttackType) {
                    case TYPE_FAKEMOVE:
                        enemyRealMoveCount++;
                        break;
                    case TYPE_REALMOVE:
                        enemyFakeMoveCount++;
                        break;
                }
            }
        }
        Board.WriteLogLine(
                "enemyFakeMoveCount = " + enemyFakeMoveCount + ", enemyRealMoveCount = " + enemyRealMoveCount
                        + ", enemyNoMoveCount = " + enemyNoMoveCount);

        // ????????????????????? = ?????????????????????????????????????????????????????????1???????????????
        if (IsEnemyLastMove()) {
            enemyMoveCount++;
            MoveValue(0, EnemyLastMoveVector());
        }

        // ????????????????????? = ???????????????????????????????????????-1???????????????, ????????????????????????????????????1???????????????
        if (IsEnemyLastAttack()) {
            Board.GetCell(EnemyLastAttackPoint()).SetValueForce(alphaSide, 0, -1);
            for (Point point : Board.GetRoundPoints(EnemyLastAttackPoint())) {
                Board.GetCell(point).AddValue(alphaSide, 0, 1);
            }
            // ????????????????????? = ????????????????????????????????????, ???????????????-2???????????????
            if (EnemyLastAttackResult().contains(Board.RESULT_SINK)) {
                Board.GetCell(EnemyLastAttackPoint()).SetValueForce(alphaSide, 0, -2);
                Board.GetCell(EnemyLastAttackPoint()).SetValueForce(alphaSide, 1, -2);
            }
            // ????????????????????? = ??????????????????????????????????????????10???????????????
            if (EnemyLastAttackResult().contains(Board.RESULT_HIT)) {
                if (fakeMoveFlag) {
                    fakeMoveEnable = false;
                }
                Board.GetCell(EnemyLastAttackPoint()).SetValue(alphaSide, 1, 20);
            }
            // ???????????????????????? = ??????????????????????????????????????????0???????????????, ????????????????????????1???????????????
            if (EnemyLastAttackResult().contains(Board.RESULT_NEAR)) {
                Board.GetCell(EnemyLastAttackPoint()).SetValue(alphaSide, 1, 0);
                for (Point point : Board.GetRoundPoints(EnemyLastAttackPoint())) {
                    Board.GetCell(point).AddValue(alphaSide, 1, 1);
                }
            }
            // ????????????????????? = ????????????????????????, ???????????????????????????????????????-1???????????????
            if (EnemyLastAttackResult().contains(Board.RESULT_NOHIT)) {
                Board.GetCell(EnemyLastAttackPoint()).SetValueForce(alphaSide, 1, -1);
                for (Point point : Board.GetRoundPoints(EnemyLastAttackPoint())) {
                    Board.GetCell(point).SetValueForce(alphaSide, 1, -1);
                }
            }
        }

        fakeMoveFlag = false;

        if (estimatedAttackedStatus == 2) {
            estimatedAttackedStatus = 0;
        }
        if (estimatedAttackedStatus == 1) {
            estimatedAttackedStatus++;
        }

        // ?????????????????????
        if (IsAllyLastAttack()) {
            // ?????????????????????
            // ?????????????????????????????? = (A) ???????????????????????????????????????????????????????????????????????????????????????????????????
            if (AllyLastAttackResult().contains(Board.RESULT_HIT)) {
                // ????????????????????? = ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? (A)
                // ?????????????????????????????? = ???????????????????????????????????????????????????
                if (IsEnemyLastMove()) {
                    Point estimatedPoint = AllyLastAttackPoint()
                            .Plus(EnemyLastMoveVector());
                    if ((enemyFakeMoveCount >= 1 && enemyRealMoveCount == 0) || !estimatedPoint.IsRange()
                            || (enemyFakeMoveCount > enemyRealMoveCount)) {
                        allyAttackType = TYPE_FAKEMOVE;
                        DoAttack(AllyLastAttackPoint());
                        return;
                    } else if ((enemyRealMoveCount >= 1 && enemyFakeMoveCount == 0)
                            || (enemyRealMoveCount > enemyFakeMoveCount)) {
                        Board.GetCell(AllyLastAttackPoint()).SetValueForce(alphaSide, 0, 0);
                        Board.GetCell(estimatedPoint).SetValueForce(alphaSide, 0, 20);
                        if (Board.IsEnableAttackPoint(alphaSide, estimatedPoint)) {
                            estimatedAttackedStatus = 1;
                            estimatedBeforePoint = AllyLastAttackPoint();
                            allyAttackType = TYPE_REALMOVE;
                            DoAttack(estimatedPoint);
                            return;
                        }
                    }
                } else {
                    if (Board.IsEnableAttackPoint(alphaSide, AllyLastAttackPoint())) {
                        allyAttackType = TYPE_NOMOVE;
                        DoAttack(AllyLastAttackPoint());
                        return;
                    }
                }
            } else {
                if (estimatedAttackedStatus == 1) {
                    estimatedAttackedStatus = 0;
                    if (Board.IsEnableAttackPoint(alphaSide, estimatedBeforePoint)) {
                        Board.GetCell(estimatedBeforePoint).SetValueForce(alphaSide, 0, 20);
                        allyAttackType = TYPE_FAKEMOVE;
                        DoAttack(estimatedBeforePoint);
                        estimatedBeforePoint = null;
                        return;
                    }
                }
                if (AllyLastAttackResult().contains(Board.RESULT_NEAR)) {

                }
            }
        }

        // ?????????????????????
        if (IsEnemyLastAttack()) {
            // ?????????????????????
            if (EnemyLastAttackResult().contains(Board.RESULT_HIT)) {
                if (fakeMoveEnable) {
                    if (Board.GetCell(EnemyLastAttackPoint()).GetHp(alphaSide) == 2) {
                        HashMap<Point, Integer> pointsValue = new HashMap<Point, Integer>();
                        for (Point point : Board.GetCrossPoints(EnemyLastAttackPoint(), 1, 1)) {
                            if (Board.IsMoveEnablePoint(alphaSide, EnemyLastAttackPoint(), point)) {
                                pointsValue.put(point, Board.GetPointDistance(point, new Point(2, 2)));
                            }
                        }
                        if (pointsValue.size() != 0) {
                            int maxValue = Collections.max(pointsValue.values());
                            for (Map.Entry<Point, Integer> pointValue : pointsValue.entrySet()) {
                                if (pointValue.getValue() == maxValue) {
                                    for (Point movePoint : Board.GetShipPoints(alphaSide)) {
                                        Point moveVector = pointValue.getKey()
                                                .Minus(EnemyLastAttackPoint());
                                        if (movePoint == EnemyLastAttackPoint()) {
                                            continue;
                                        }
                                        if (!movePoint.Plus(moveVector).IsRange()) {
                                            continue;
                                        }
                                        if (Board.IsMoveEnableVector(alphaSide, movePoint, moveVector)) {
                                            fakeMoveFlag = true;
                                            DoMove(movePoint, movePoint.Plus(moveVector));
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (prepareTurned && Board.IsEnableAttackPoint(alphaSide, preparePoint)) {
            prepareTurned = false;
            allyAttackType = TYPE_HIT;
            DoAttack(preparePoint);
            preparePoint = null;
            return;
        }

        ArrayList<Point> maxValuePoints = new ArrayList<Point>(
                Board.GetPointValues(alphaSide, null, 0, 1).keySet());
        if (Board.GetCell(maxValuePoints.get(0)).GetValue(alphaSide, 0) >= 5) {
            for (Point point : maxValuePoints) {
                if (Board.IsEnableAttackPoint(alphaSide, point)) {
                    allyAttackType = TYPE_SEARCH;
                    DoAttack(point);
                    return;
                }
            }
            for (Point point : maxValuePoints) {
                if (Board.GetCell(point).IsAlive(alphaSide)) {
                    HashMap<Point, Integer> crossPointValues = new HashMap<Point, Integer>();
                    for (Point crossPoint : Board.GetFilterMoveEnablePoints(alphaSide, point,
                            Board.GetCrossPoints(point, 1, 1))) {
                        crossPointValues.put(crossPoint,
                                Board.GetCell(crossPoint).GetValue(alphaSide, 1));
                    }
                    if (crossPointValues.size() != 0) {
                        int value = Collections.max(crossPointValues.values());
                        for (Map.Entry<Point, Integer> crossPointValue : crossPointValues.entrySet()) {
                            if (crossPointValue.getValue() == value) {
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
                                if (!Board.IsMoveEnableVector(alphaSide, movePoint, moveVector)) {
                                    moveVector = null;
                                }
                            }
                            if (moveVector != null) {
                                DoMove(movePoint, movePoint.Plus(moveVector));
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (Board.GetMaxValuePoints(alphaSide, true, 0).size() != 0) {
            allyAttackType = TYPE_SEARCH;
            DoAttack(Board.GetRandomPoint(Board.GetMaxValuePoints(alphaSide, true, 0)));
            return;
        } else {
            Board.WriteDisableTurn();
        }
    }
}