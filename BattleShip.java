import java.util.*;

class Cell {
    private Integer alphaHp;
    private Integer bravoHp;

    private Integer alphaValue;
    private Integer bravoValue;

    private boolean alphaIsAttack;
    private boolean bravoIsAttack;

    Cell() {
        alphaHp = -1;
        bravoHp = -1;
        alphaValue = 0;
        bravoValue = 0;
        alphaIsAttack = false;
        bravoIsAttack = false;
    }

    public Integer GetHp(boolean alphaSide) {
        if (alphaSide) {
            return alphaHp;
        } else {
            return bravoHp;
        }
    }

    public void SetHp(boolean alphaSide, Integer hp) {
        if (alphaSide) {
            alphaHp = hp;
        } else {
            bravoHp = hp;
        }
    }

    public Integer GetValue(boolean alphaSide) {
        if (alphaSide) {
            return alphaValue;
        } else {
            return bravoValue;
        }
    }

    public void SetValue(boolean alphaSide, Integer value) {
        if (alphaSide) {
            alphaValue = value;
        } else {
            bravoValue = value;
        }
    }

    public void SetIsAttak(boolean alphaSide, boolean isAttack) {
        if (isAttack) {
            if (GetIsEmpty(alphaSide)) {
                if (alphaSide) {
                    alphaIsAttack = true;
                } else {
                    bravoIsAttack = true;
                }
            }
        } else {
            if (alphaSide) {
                alphaIsAttack = false;
            } else {
                bravoIsAttack = false;
            }
        }
    }

    public boolean GetIsAttack(boolean alphaSide) {
        if (alphaSide) {
            return alphaIsAttack;
        } else {
            return bravoIsAttack;
        }
    }

    public boolean GetIsAlive(boolean alphaSide) {
        if (alphaSide) {
            return alphaHp > 0;
        } else {
            return bravoHp > 0;
        }
    }

    public boolean GetIsEmpty(boolean alphaSide) {
        if (alphaSide) {
            return alphaHp == -1;
        } else {
            return bravoHp == -1;
        }
    }
}

class Point {
    public Integer x;
    public Integer y;

    Point(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Point Plus(Point point) {
        return new Point(this.x + point.x, this.y + point.y);
    }

    public Point Minus(Point point) {
        return new Point(this.x - point.x, this.y - point.y);
    }

    @Override
    public String toString() {
        return "(x, y) = " + x + ", " + y;
    }
}

class Board {
    private static final Integer boardSize = 5;

    private static Cell[][] cells;
    private static Integer turnCount;

    private static boolean alphaWin;
    private static boolean bravoWin;

    private static Point lastAlphaAttackPoint;
    private static Integer lastAlphaAttackResult;
    private static Point lastAlphaMoveVector;

    private static Point lastBravoAttackPoint;
    private static Integer lastBravoAttackResult;
    private static Point lastBravoMoveVector;

    private static boolean visibleLog;

    Board() {
        Initialize(true);
    }

    public static void Initialize(boolean visibleLog) {
        cells = new Cell[GetBoardSize()][GetBoardSize()];
        for (Integer x = 0; x < GetBoardSize(); x++) {
            for (Integer y = 0; y < GetBoardSize(); y++) {
                SetCell(x, y, new Cell());
            }
        }
        turnCount = 0;

        alphaWin = false;
        bravoWin = false;

        lastAlphaAttackPoint = null;
        lastAlphaAttackResult = null;
        lastAlphaMoveVector = null;

        lastBravoAttackPoint = null;
        lastBravoAttackResult = null;
        lastBravoMoveVector = null;

        Board.visibleLog = visibleLog;
    }

    public static boolean GetAlphaWin() {
        return alphaWin;
    }

    public static boolean GetBravoWin() {
        return bravoWin;
    }

    public static void SetTurnCount() {
        turnCount++;
    }

    public static Integer GetTurnCount() {
        return turnCount;
    }

    public static Integer GetBoardSize() {
        return boardSize;
    }

    public static Cell GetCell(Point point) {
        return cells[point.x][point.y];
    }

    public static Cell GetCell(Integer x, Integer y) {
        return cells[x][y];
    }

    public static void SetCell(Point point, Cell cell) {
        cells[point.x][point.y] = cell;
    }

    public static void SetCell(int x, Integer y, Cell cell) {
        cells[x][y] = cell;
    }

    public static Point GetLastAttackPoint(boolean alphaSide) {
        if (alphaSide) {
            return lastAlphaAttackPoint;
        } else {
            return lastBravoAttackPoint;
        }
    }

    public static Integer GetLastAttackResult(boolean alphaSide) {
        if (alphaSide) {
            return lastAlphaAttackResult;
        } else {
            return lastBravoAttackResult;
        }
    }

    public static Point GetLastMoveVector(boolean alphaSide) {
        if (alphaSide) {
            return lastAlphaMoveVector;
        } else {
            return lastBravoMoveVector;
        }
    }

    public static void LogSide(boolean alphaSide) {
        if (visibleLog) {
            if (alphaSide) {
                System.out.print("【α");
            } else {
                System.out.print("【β");
            }
        }
    }

    public static void LogLine(String line) {
        if (visibleLog) {
            System.out.println(line);
        }
    }

    public static void Log(String line) {
        if (visibleLog) {
            System.out.print(line);
        }
    }

    // ゲーム続行の可否
    public static boolean IsContinue(boolean interrupt) {
        Integer alphaCount = ShipPoints(true).size();
        Integer bravoCount = ShipPoints(false).size();
        Integer alphaSumHp = 0;
        for (Point point : ShipPoints(true)) {
            alphaSumHp += GetCell(point).GetHp(true);
        }
        Integer bravoSumHp = 0;
        for (Point point : ShipPoints(false)) {
            bravoSumHp += GetCell(point).GetHp(false);
        }
        if (!interrupt) {
            SetTurnCount();
        }
        LogLine("--------------------");
        LogLine("【戦況】 " + Board.GetTurnCount() + "ターン目");
        LogLine("α残機 = " + alphaCount + " (総HP : " + alphaSumHp + ")");
        LogLine("β残機 = " + bravoCount + " (総HP : " + bravoSumHp + ")");
        if (alphaCount == 0) {
            LogLine("αが全滅しました");
            LogLine("βの勝利です");
            alphaWin = false;
            bravoWin = true;
            return false;
        }
        if (bravoCount == 0) {
            LogLine("βが全滅しました");
            LogLine("αの勝利です");
            alphaWin = true;
            bravoWin = false;
            return false;
        }
        if (interrupt) {
            if (alphaSumHp > bravoSumHp) {
                LogLine("αの勝利です");
                alphaWin = true;
                bravoWin = false;
            } else if (bravoSumHp > alphaSumHp) {
                LogLine("βの勝利です");
                alphaWin = false;
                bravoWin = true;
            } else {
                LogLine("引き分けです");
                alphaWin = false;
                bravoWin = false;
            }
            return false;
        }
        return true;
    }

    // 重複を除くランダムなポイントリスト
    public static ArrayList<Point> RandomPoints(Integer count) {
        ArrayList<Point> points = new ArrayList<Point>();
        Random random = new Random();
        HashMap<Integer, Integer> randomPoints = new HashMap<Integer, Integer>();
        while (randomPoints.size() != count) {
            randomPoints.put(random.nextInt(Board.GetBoardSize()), random.nextInt(Board.GetBoardSize()));
        }
        for (Map.Entry<Integer, Integer> randomPoint : randomPoints.entrySet()) {
            points.add(new Point(randomPoint.getKey(), randomPoint.getValue()));
        }
        return points;
    }

    // ランダムな4箇所に配置
    public static void SetRandom4Points(boolean alphaSide) {
        for (Point point : RandomPoints(4)) {
            GetCell(point).SetHp(alphaSide, 3);
        }
    }

    // 値が最大であるポイントリスト
    public static ArrayList<Point> MaxValuePoints(boolean alphaSide, boolean isAttackEnable) {
        HashMap<Point, Integer> pointsValue = new HashMap<Point, Integer>();
        for (Integer x = 0; x < Board.GetBoardSize(); x++) {
            for (Integer y = 0; y < Board.GetBoardSize(); y++) {
                if ((isAttackEnable && Board.IsAttackEnablePoint(alphaSide, new Point(x, y))) || !isAttackEnable) {
                    pointsValue.put(new Point(x, y), Board.GetCell(x, y).GetValue(alphaSide));
                }
            }
        }
        Integer maxValue = Collections.max(pointsValue.values());
        ArrayList<Point> points = new ArrayList<Point>();
        for (Map.Entry<Point, Integer> pointValue : pointsValue.entrySet()) {
            if (pointValue.getValue() == maxValue) {
                points.add(pointValue.getKey());
            }
        }
        return points;
    }

    // 指定ポイントから8方向のポイントリスト
    public static ArrayList<Point> PointRound(Point point) {
        ArrayList<Point> points = new ArrayList<Point>();
        if (point.x > 0) {
            points.add(new Point(point.x - 1, point.y));
        }
        if (point.x < Board.GetBoardSize() - 1) {
            points.add(new Point(point.x + 1, point.y));
        }
        if (point.y > 0) {
            points.add(new Point(point.x, point.y - 1));
        }
        if (point.y < Board.GetBoardSize() - 1) {
            points.add(new Point(point.x, point.y + 1));
        }
        if (point.x > 0 && point.y > 0) {
            points.add(new Point(point.x - 1, point.y - 1));
        }
        if (point.x > 0 && point.y < Board.GetBoardSize() - 1) {
            points.add(new Point(point.x - 1, point.y + 1));
        }
        if (point.x < Board.GetBoardSize() - 1 && point.y > 0) {
            points.add(new Point(point.x + 1, point.y - 1));
        }
        if (point.x < Board.GetBoardSize() - 1 && point.y < Board.GetBoardSize() - 1) {
            points.add(new Point(point.x + 1, point.y + 1));
        }
        return points;
    }

    // 指定ポイントから4方向のポイントリスト
    public static ArrayList<Point> PointCross(Point point, Integer length) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (Integer i = 1; i <= length; i++) {
            if (point.x > i - 1) {
                points.add(new Point(point.x - i, point.y));
            }
            if (point.x < Board.GetBoardSize() - i) {
                points.add(new Point(point.x + i, point.y));
            }
            if (point.y > i - 1) {
                points.add(new Point(point.x, point.y - i));
            }
            if (point.y < Board.GetBoardSize() - i) {
                points.add(new Point(point.x, point.y + i));
            }
        }
        return points;
    }

    // 指定ポイントから指定ポイントへの移動可否
    public static boolean IsMoveEnablePoint(boolean alphaSide, Point oldPoint, Point newPoint) {
        if (Board.GetCell(oldPoint).GetIsAlive(alphaSide) && Board.GetCell(newPoint).GetIsEmpty(alphaSide)) {
            if (PointDistance(oldPoint, newPoint) == 1) {
                return true;
            } else if (Math.abs(oldPoint.x - newPoint.x) == 2 && oldPoint.y == newPoint.y) {
                return true;
            } else if (Math.abs(oldPoint.y - newPoint.y) == 2 && oldPoint.x == newPoint.x) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // 指定ポイントから指定ベクトル方向への移動可否
    public static boolean IsMoveEnableVector(boolean alphaSide, Point oldPoint, Point vectorPoint) {
        return IsMoveEnablePoint(alphaSide, oldPoint, oldPoint.Plus(vectorPoint));
    }

    // 指定ポイントから指定ポイントへの距離（X距離 + Y距離）
    public static Integer PointDistance(Point aPoint, Point bPoint) {
        return (Math.abs(aPoint.x - bPoint.x) + Math.abs(aPoint.y - bPoint.y));
    }

    // 指定ポイントから指定ポイントへの移動
    public static boolean MovePoint(boolean alphaSide, Point oldPoint, Point newPoint) {
        LogSide(alphaSide);
        if (IsMoveEnablePoint(alphaSide, oldPoint, newPoint)) {
            LogLine("移動】 " + oldPoint + " → " + newPoint);
            Board.GetCell(newPoint).SetHp(alphaSide, Board.GetCell(oldPoint).GetHp(alphaSide));
            Board.GetCell(oldPoint).SetHp(alphaSide, -1);
            if (alphaSide) {
                lastAlphaAttackPoint = null;
                lastAlphaAttackResult = null;
                lastAlphaMoveVector = newPoint.Minus(oldPoint);
            } else {
                lastBravoAttackPoint = null;
                lastBravoAttackResult = null;
                lastBravoMoveVector = newPoint.Minus(oldPoint);
            }
            return true;
        } else {
            LogLine("移動】拒否されました");
            return false;
        }
    }

    // 指定ベクトル方向への移動（強制）
    public static void MovePointForce(boolean alphaSide, Point vectorPoint) {
        LogSide(alphaSide);
        LogLine("移動】 " + vectorPoint);
        if (alphaSide) {
            lastAlphaAttackPoint = null;
            lastAlphaAttackResult = null;
            lastAlphaMoveVector = vectorPoint;
        } else {
            lastBravoAttackPoint = null;
            lastBravoAttackResult = null;
            lastBravoMoveVector = vectorPoint;
        }
    }

    // 指定ポイントから指定ベクトル方向への移動
    public static boolean MoveVector(boolean alphaSide, Point oldPoint, Point vectorPoint) {
        return MovePoint(alphaSide, oldPoint, oldPoint.Plus(vectorPoint));
    }

    // 指定ポイントへ最も近い戦艦のポイントリスト
    public static ArrayList<Point> ShortPoint(boolean alphaSide, Point point) {
        HashMap<Point, Integer> pointsDistance = new HashMap<Point, Integer>();
        for (Point shipPoint : ShipPoints(alphaSide)) {
            pointsDistance.put(shipPoint, PointDistance(shipPoint, point));
        }
        Integer shortDistance = Collections.min(pointsDistance.values());
        ArrayList<Point> points = new ArrayList<Point>();
        for (Map.Entry<Point, Integer> pointDistance : pointsDistance.entrySet()) {
            if (pointDistance.getValue() == shortDistance) {
                points.add(pointDistance.getKey());
            }
        }
        return points;
    }

    // 値をすべて初期化
    public static void ClearValue(boolean alphaSide) {
        for (Integer x = 0; x < Board.GetBoardSize(); x++) {
            for (Integer y = 0; y < Board.GetBoardSize(); y++) {
                Board.GetCell(x, y).SetValue(alphaSide, 0);
            }
        }
    }

    // 値をすべて平均化
    public static void NormalizeValue(boolean alphaSide) {
        for (Integer x = 0; x < Board.GetBoardSize(); x++) {
            for (Integer y = 0; y < Board.GetBoardSize(); y++) {
                Integer value = 3;
                if (x != 0 && x != Board.GetBoardSize() - 1) {
                    value += 2;
                }
                if (y != 0 && y != Board.GetBoardSize() - 1) {
                    value += 2;
                }
                Board.GetCell(x, y).SetValue(alphaSide, value);
            }
        }
    }

    // 戦艦のポイントリスト
    public static ArrayList<Point> ShipPoints(boolean alphaSide) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (Integer x = 0; x < Board.GetBoardSize(); x++) {
            for (Integer y = 0; y < Board.GetBoardSize(); y++) {
                if (Board.GetCell(x, y).GetIsAlive(alphaSide)) {
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }

    // 攻撃可能範囲の検索
    public static void AttackEnableSearch(boolean alphaSide) {
        for (Integer x = 0; x < Board.GetBoardSize(); x++) {
            for (Integer y = 0; y < Board.GetBoardSize(); y++) {
                Board.GetCell(x, y).SetIsAttak(alphaSide, false);
            }
        }
        for (Point shipPoint : Board.ShipPoints(alphaSide)) {
            for (Point point : PointRound(shipPoint)) {
                Board.GetCell(point).SetIsAttak(alphaSide, true);
            }
        }
        for (Point shipPoint : Board.ShipPoints(!alphaSide)) {
            if (Board.GetCell(shipPoint).GetHp(!alphaSide) == 0) {
                Board.GetCell(shipPoint).SetIsAttak(alphaSide, false);
            }
        }
    }

    // 攻撃可能なポイントリスト
    public static ArrayList<Point> AttackEnablePoints(boolean alphaSide) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (Integer x = 0; x < Board.GetBoardSize(); x++) {
            for (Integer y = 0; y < Board.GetBoardSize(); y++) {
                if (Board.GetCell(x, y).GetIsAttack(alphaSide)) {
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }

    // 指定ポイントへの攻撃可否
    public static boolean IsAttackEnablePoint(boolean alphaSide, Point point) {
        AttackEnableSearch(alphaSide);
        return Board.GetCell(point).GetIsAttack(alphaSide);
    }

    // 指定ポイントへの攻撃
    public static boolean AttackPoint(boolean alphaSide, Point point, boolean judgeResult) {
        LogSide(alphaSide);
        if (IsAttackEnablePoint(alphaSide, point)) {
            Integer attackResult = null;
            LogLine("攻撃】" + point);
            if (judgeResult) {
                if (Board.GetCell(point).GetIsAlive(!alphaSide)) {
                    Board.GetCell(point).SetHp(!alphaSide, Board.GetCell(point).GetHp(!alphaSide) - 1);
                    // 命中！
                    attackResult = 2;
                    LogLine("命中！");
                    if (Board.GetCell(point).GetHp(!alphaSide) == 0) {
                        // 撃沈！
                        attackResult = 3;
                        LogLine("撃沈！");
                    }
                } else {
                    // ハズレ！
                    attackResult = 0;
                    LogLine("ハズレ！");
                }
                for (Point roundPoint : PointRound(point)) {
                    if (Board.GetCell(roundPoint).GetIsAlive(!alphaSide)) {
                        // 波高し！
                        attackResult = 1;
                        LogLine("波高し！");
                    }
                }
            }
            if (alphaSide) {
                lastAlphaAttackPoint = point;
                lastAlphaAttackResult = attackResult;
                lastAlphaMoveVector = null;
            } else {
                lastBravoAttackPoint = point;
                lastBravoAttackResult = attackResult;
                lastBravoMoveVector = null;
            }
            return true;
        } else {
            LogLine("攻撃】 拒否されました");
            if (alphaSide) {
                lastAlphaAttackPoint = null;
                lastAlphaAttackResult = null;
                lastAlphaMoveVector = null;
            } else {
                lastBravoAttackPoint = null;
                lastBravoAttackResult = null;
                lastBravoMoveVector = null;
            }
            return false;
        }
    }

    // 指定ポイントへの攻撃結果を上書き
    public static void AttackResultTransfer(boolean alphaSide, Integer attackResult) {
        switch (attackResult) {
            case 3:
                LogLine("撃沈！");
                break;
            case 2:
                LogLine("命中！");
                break;
            case 1:
                LogLine("波高し！");
                break;
            case 0:
                LogLine("ハズレ！");
                break;
        }
        if (alphaSide) {
            lastAlphaAttackResult = attackResult;
        } else {
            lastBravoAttackResult = attackResult;
        }
    }

    // 指定ポイントへの攻撃（強制）
    public static void AttackPointForce(boolean alphaSide, Point point) {
        LogSide(alphaSide);
        Integer attackResult = null;
        LogLine("攻撃】" + point);
        if (Board.GetCell(point).GetIsAlive(!alphaSide)) {
            Board.GetCell(point).SetHp(!alphaSide, Board.GetCell(point).GetHp(!alphaSide) - 1);
            // 命中！
            attackResult = 2;
            LogLine("命中！");
            if (Board.GetCell(point).GetHp(!alphaSide) == 0) {
                // 撃沈！
                attackResult = 3;
                LogLine("撃沈！");
            }
        } else {
            // ハズレ！
            attackResult = 0;
            LogLine("ハズレ！");
        }
        for (Point roundPoint : PointRound(point)) {
            if (Board.GetCell(roundPoint).GetIsAlive(!alphaSide)) {
                // 波高し！
                attackResult = 1;
                LogLine("波高し！");
            }
        }
        if (alphaSide) {
            lastAlphaAttackPoint = point;
            lastAlphaAttackResult = attackResult;
            lastAlphaMoveVector = null;
        } else {
            lastBravoAttackPoint = point;
            lastBravoAttackResult = attackResult;
            lastBravoMoveVector = null;
        }
    }

    // 盤面にHPを表示
    public static void WriteBoardHp(boolean alphaSide) {
        LogSide(alphaSide);
        LogLine("盤面】HP");
        Log("  ");
        for (Integer i = 0; i < Board.GetBoardSize(); i++) {
            if (i != 0) {
                Log("|");
            }
            Log(i.toString());
        }
        LogLine("");
        for (Integer y = 0; y < Board.GetBoardSize(); y++) {
            Log(y + "|");
            for (Integer x = 0; x < Board.GetBoardSize(); x++) {
                if (Board.GetCell(x, y).GetHp(alphaSide) != -1) {
                    Log(Board.GetCell(x, y).GetHp(alphaSide).toString());
                } else {
                    Log(" ");
                }
                Log(" ");
            }
            LogLine("");
        }
    }

    // 盤面に攻撃可能範囲を表示
    public static void WriteBoardIsAttack(boolean alphaSide) {
        LogSide(alphaSide);
        AttackEnableSearch(alphaSide);
        LogLine("盤面】攻撃可能範囲");
        Log("  ");
        for (Integer i = 0; i < Board.GetBoardSize(); i++) {
            if (i != 0) {
                Log("|");
            }
            Log(i.toString());
        }
        LogLine("");
        for (Integer y = 0; y < Board.GetBoardSize(); y++) {
            Log(y.toString() + "|");
            for (Integer x = 0; x < Board.GetBoardSize(); x++) {
                if (Board.GetCell(x, y).GetIsAttack(alphaSide)) {
                    Log("*");
                } else {
                    Log(" ");
                }
                Log(" ");
            }
            LogLine("");
        }
    }

    public static boolean IsLastMove(boolean alphaSide) {
        if (alphaSide) {
            return (lastAlphaMoveVector != null);
        } else {
            return (lastBravoMoveVector != null);
        }
    }

    public static boolean IsLastAttack(boolean alphaSide) {
        if (alphaSide) {
            return (lastAlphaAttackPoint != null);
        } else {
            return (lastBravoAttackPoint != null);
        }
    }

    // ポイントリストからランダムに選ぶ
    public static Point RandomGet(ArrayList<Point> points) {
        Random random = new Random();
        return points.get(random.nextInt(points.size()));
    }
}

class Interface {
    public Integer allyCount;
    public Integer allySumHp;
    public Integer enemyCount;
    public Integer enemySumHp;

    public final boolean alphaSide;
    public final boolean isEnemySecret;

    Interface(boolean alphaSide, boolean isEnemySecret) {
        this.alphaSide = alphaSide;
        this.isEnemySecret = isEnemySecret;
        allyCount = 4;
        allySumHp = 12;
        enemyCount = 4;
        enemySumHp = 12;
    }

    public void DoMove(Point oldPoint, Point newPoint) {
        Board.LogLine(newPoint.Minus(oldPoint) + " に移動！");
        Board.MovePoint(alphaSide, oldPoint, newPoint);
    }

    public void DoAttack(Point point) {
        Board.LogLine(point + " に魚雷発射！");
        Board.AttackPoint(alphaSide, point, !isEnemySecret);
    }

    public void DoMoveForce(Point vectorPoint) {
        Board.LogLine(vectorPoint + " に移動！");
        Board.MovePointForce(alphaSide, vectorPoint);
    }

    public void DoAttackForce(Point point) {
        Board.LogLine(point + " に魚雷発射！");
        Board.AttackPointForce(alphaSide, point);
    }
}
