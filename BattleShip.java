import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Collections;

class Cell {
    private Integer alphaHp;
    private Integer bravoHp;

    private Integer alphaValue;
    private Integer bravoValue;

    private boolean alphaCanAttack;
    private boolean bravoCanAttack;

    Cell() {
        alphaHp = -1;
        bravoHp = -1;
        alphaCanAttack = false;
        bravoCanAttack = false;
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

    public void SetCanAttak(boolean alphaSide, boolean canAttack) {
        if (canAttack) {
            if (isEmpty(alphaSide)) {
                if (alphaSide) {
                    alphaCanAttack = true;
                } else {
                    bravoCanAttack = true;
                }
            }
        } else {
            if (alphaSide) {
                alphaCanAttack = false;
            } else {
                bravoCanAttack = false;
            }
        }
    }

    public boolean GetCanAttack(boolean alphaSide) {
        if (alphaSide) {
            return alphaCanAttack;
        } else {
            return bravoCanAttack;
        }
    }

    public boolean isAlive(boolean alphaSide) {
        if (alphaSide) {
            return alphaHp > 0;
        } else {
            return bravoHp > 0;
        }
    }

    public boolean isEmpty(boolean alphaSide) {
        if (alphaSide) {
            return alphaHp == -1 && bravoHp != 0;
        } else {
            return bravoHp == -1 && alphaHp != 0;
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

class BoardCells {
    private static final Integer boardSize = 5;
    private static Cell[][] boardCells;
    private static Integer turnCount;

    private static Point lastAlphaAttackPoint;
    private static Integer lastAlphaAttackResult;
    private static Point lastAlphaMoveVector;

    private static Point lastBravoAttackPoint;
    private static Integer lastBravoAttackResult;
    private static Point lastBravoMoveVector;

    BoardCells() {
        boardCells = new Cell[boardSize][boardSize];
        for (Integer x = 0; x < boardSize; x++) {
            for (Integer y = 0; y < boardSize; y++) {
                SetCell(x, y, new Cell());
            }
        }
        turnCount = 0;
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
        return boardCells[point.x][point.y];
    }

    public static Cell GetCell(int x, Integer y) {
        return boardCells[x][y];
    }

    public static void SetCell(Point point, Cell cell) {
        boardCells[point.x][point.y] = cell;
    }

    public static void SetCell(int x, Integer y, Cell cell) {
        boardCells[x][y] = cell;
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

    // ゲーム続行の可否
    public static boolean IsContinue(boolean interrupt) {
        Integer alphaCount = ShipPoints(true).size();
        Integer bravoCount = ShipPoints(false).size();
        Integer alphaSumHp = 0;
        for (Point point : ShipPoints(true)) {
            alphaSumHp += BoardCells.GetCell(point).GetHp(true);
        }
        Integer bravoSumHp = 0;
        for (Point point : ShipPoints(false)) {
            bravoSumHp += BoardCells.GetCell(point).GetHp(false);
        }
        if (!interrupt) {
            BoardCells.SetTurnCount();
        }
        System.out.println("【戦況】 " + BoardCells.GetTurnCount() + "ターン目");
        System.out.println("α残機 = " + alphaCount + " (総HP : " + alphaSumHp + ")");
        System.out.println("β残機 = " + bravoCount + " (総HP : " + bravoSumHp + ")");
        if (alphaCount == 0) {
            System.out.println("αが全滅しました");
            System.out.println("βの勝利です");
            return false;
        }
        if (bravoCount == 0) {
            System.out.println("βが全滅しました");
            System.out.println("αの勝利です");
            return false;
        }
        if (interrupt) {
            if (alphaSumHp > bravoSumHp) {
                System.out.println("αの勝利です");
            } else if (bravoSumHp > alphaSumHp) {
                System.out.println("βの勝利です");
            } else {
                System.out.println("引き分けです");
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
            randomPoints.put(random.nextInt(BoardCells.GetBoardSize()), random.nextInt(BoardCells.GetBoardSize()));
        }
        for (Map.Entry<Integer, Integer> randomPoint : randomPoints.entrySet()) {
            points.add(new Point(randomPoint.getKey(), randomPoint.getValue()));
        }
        return points;

    }

    // 指定ポイントから8方向のポイントリスト
    public static ArrayList<Point> PointRound(Point point) {
        ArrayList<Point> points = new ArrayList<Point>();
        if (point.x > 0) {
            points.add(new Point(point.x - 1, point.y));
        }
        if (point.x < BoardCells.GetBoardSize() - 1) {
            points.add(new Point(point.x + 1, point.y));
        }
        if (point.y > 0) {
            points.add(new Point(point.x, point.y - 1));
        }
        if (point.y < BoardCells.GetBoardSize() - 1) {
            points.add(new Point(point.x, point.y + 1));
        }
        if (point.x > 0 && point.y > 0) {
            points.add(new Point(point.x - 1, point.y - 1));
        }
        if (point.x > 0 && point.y < BoardCells.GetBoardSize() - 1) {
            points.add(new Point(point.x - 1, point.y + 1));
        }
        if (point.x < BoardCells.GetBoardSize() - 1 && point.y > 0) {
            points.add(new Point(point.x + 1, point.y - 1));
        }
        if (point.x < BoardCells.GetBoardSize() - 1 && point.y < BoardCells.GetBoardSize() - 1) {
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
            if (point.x < BoardCells.GetBoardSize() - i) {
                points.add(new Point(point.x + i, point.y));
            }
            if (point.y > i - 1) {
                points.add(new Point(point.x, point.y - i));
            }
            if (point.y < BoardCells.GetBoardSize() - i) {
                points.add(new Point(point.x, point.y + i));
            }
        }
        return points;
    }

    // 指定ポイントから指定ポイントへの移動可否
    public static boolean CanMovePoint(boolean alphaSide, Point oldPoint, Point newPoint) {
        if (BoardCells.GetCell(oldPoint).isAlive(alphaSide) && BoardCells.GetCell(newPoint).isEmpty(alphaSide)) {
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
    public static boolean CanMoveVector(boolean alphaSide, Point oldPoint, Point vectorPoint) {
        return CanMovePoint(alphaSide, oldPoint, oldPoint.Plus(vectorPoint));
    }

    // 指定ポイントから指定ポイントへの距離（X距離 + Y距離）
    public static Integer PointDistance(Point aPoint, Point bPoint) {
        return (Math.abs(aPoint.x - bPoint.x) + Math.abs(aPoint.y - bPoint.y));
    }

    // 指定ポイントから指定ポイントへの移動
    public static boolean MovePoint(boolean alphaSide, Point oldPoint, Point newPoint) {
        if (CanMovePoint(alphaSide, oldPoint, newPoint)) {
            System.out.println("【戦艦移動】 " + oldPoint + " → " + newPoint);
            BoardCells.GetCell(newPoint).SetHp(alphaSide, BoardCells.GetCell(oldPoint).GetHp(alphaSide));
            BoardCells.GetCell(oldPoint).SetHp(alphaSide, -1);
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
            System.out.println("【戦艦移動】拒否されました");
            return false;
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

    // 戦艦のポイントリスト
    public static ArrayList<Point> ShipPoints(boolean alphaSide) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (Integer x = 0; x < BoardCells.GetBoardSize(); x++) {
            for (Integer y = 0; y < BoardCells.GetBoardSize(); y++) {
                if (BoardCells.GetCell(x, y).isAlive(alphaSide)) {
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }

    // 攻撃可能範囲の検索
    public static void CanAttackSearch(boolean alphaSide) {
        for (Integer x = 0; x < BoardCells.GetBoardSize(); x++) {
            for (Integer y = 0; y < BoardCells.GetBoardSize(); y++) {
                BoardCells.GetCell(x, y).SetCanAttak(alphaSide, false);
            }
        }
        for (Integer x = 0; x < BoardCells.GetBoardSize(); x++) {
            for (Integer y = 0; y < BoardCells.GetBoardSize(); y++) {
                if (BoardCells.GetCell(x, y).isAlive(alphaSide)) {
                    for (Point point : PointRound(new Point(x, y))) {
                        BoardCells.GetCell(point).SetCanAttak(alphaSide, true);
                    }
                }
            }
        }
    }

    // 指定ポイントへの攻撃可否
    public static boolean CanAttackPoint(boolean alphaSide, Point point) {
        CanAttackSearch(alphaSide);
        return BoardCells.GetCell(point).GetCanAttack(alphaSide);
    }

    // 指定ポイントへの攻撃
    public static boolean AttackPoint(boolean alphaSide, Point point) {
        if (alphaSide) {
            System.out.print("【α");
        } else {
            System.out.print("【β");
        }
        if (CanAttackPoint(alphaSide, point)) {
            Integer attackResult = 0;
            System.out.println("攻撃】" + point);
            if (BoardCells.GetCell(point).isAlive(!alphaSide)) {
                BoardCells.GetCell(point).SetHp(!alphaSide, BoardCells.GetCell(point).GetHp(!alphaSide) - 1);
                // 命中！
                attackResult = 2;
                System.out.println("命中！");
                if (BoardCells.GetCell(point).GetHp(!alphaSide) == 0) {
                    // 撃沈！
                    attackResult = 3;
                    System.out.println("撃沈！");
                }
            } else {
                // ハズレ！
                attackResult = 0;
                System.out.println("ハズレ！");
            }
            for (Point roundPoint : PointRound(point)) {
                if (BoardCells.GetCell(roundPoint).isAlive(!alphaSide)) {
                    // 波高し！
                    attackResult = 1;
                    System.out.println("波高し！");
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
            System.out.println("攻撃】 拒否されました");
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

    // 盤面にHPを表示
    public static void WriteBoardHp(boolean alphaSide) {
        if (alphaSide) {
            System.out.print("【α");
        } else {
            System.out.print("【β");
        }
        System.out.println("盤面】HP");
        System.out.print("  ");
        for (Integer i = 0; i < BoardCells.GetBoardSize(); i++) {
            if (i != 0) {
                System.out.print("|");
            }
            System.out.print((i));
        }
        System.out.println();
        for (Integer y = 0; y < BoardCells.GetBoardSize(); y++) {
            System.out.print(y + "|");
            for (Integer x = 0; x < BoardCells.GetBoardSize(); x++) {
                if (BoardCells.GetCell(x, y).GetHp(alphaSide) != -1) {
                    System.out.print(BoardCells.GetCell(x, y).GetHp(alphaSide));
                } else {
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    // 盤面に攻撃可能範囲を表示
    public static void WriteBoardCanAttack(boolean alphaSide) {
        if (alphaSide) {
            System.out.print("【α");
        } else {
            System.out.print("【β");
        }
        CanAttackSearch(alphaSide);
        System.out.println("盤面】攻撃可能範囲");
        System.out.print("  ");
        for (Integer i = 0; i < BoardCells.GetBoardSize(); i++) {
            if (i != 0) {
                System.out.print("|");
            }
            System.out.print((i));
        }
        System.out.println();
        for (Integer y = 0; y < BoardCells.GetBoardSize(); y++) {
            System.out.print(y + "|");
            for (Integer x = 0; x < BoardCells.GetBoardSize(); x++) {
                if (BoardCells.GetCell(x, y).GetCanAttack(alphaSide)) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

class Algorithm {
    private Integer allyCount;
    private Integer allySumHp;
    private Integer enemyCount;
    private Integer enemySumHp;

    private final boolean alphaSide;

    Algorithm(boolean alphaSide) {
        super();
        this.alphaSide = alphaSide;
        allyCount = 4;
        allySumHp = 12;
        enemyCount = 4;
        enemySumHp = 12;
    }

    public void Think() {
        BoardCells.CanAttackSearch(alphaSide);
        if (BoardCells.GetLastAttackResult(!alphaSide) != null) {
            // 敵に攻撃された
            switch (BoardCells.GetLastAttackResult(!alphaSide)) {
                case 3:
                case 2:
                    allySumHp--;
                    if (BoardCells.GetLastAttackResult(!alphaSide) == 3) {
                        allyCount--;
                        // 敵に撃沈された
                    } else {
                        // 敵に命中された
                        ArrayList<Point> points = new ArrayList<Point>();
                        for (Point point : BoardCells.PointCross(BoardCells.GetLastAttackPoint(!alphaSide), 2)) {
                            if (BoardCells.CanMovePoint(alphaSide, BoardCells.GetLastAttackPoint(!alphaSide), point)) {
                                points.add(point);
                            }
                        }
                        // 移動できる範囲からランダムに移動
                        Random random = new Random();
                        DoMove(BoardCells.GetLastAttackPoint(!alphaSide), points.get(random.nextInt(points.size())));
                    }
                    break;
                case 1:
                    // 波高しされた

                    break;
            }
        }
        if (BoardCells.GetLastAttackResult(alphaSide) != null) {
            // 敵を攻撃した
            switch (BoardCells.GetLastAttackResult(alphaSide)) {
                case 3:
                case 2:
                    enemySumHp--;
                    if (BoardCells.GetLastAttackResult(alphaSide) == 3) {
                        enemyCount--;
                        // 敵を撃沈した
                    } else {
                        // 敵を命中した
                        if (BoardCells.GetLastMoveVector(!alphaSide) == null) {
                            // 敵が移動しなかった
                            DoAttack(BoardCells.GetLastAttackPoint(alphaSide));
                            return;
                        } else {
                            // 敵が移動した
                            if (enemyCount == 1) {
                                // 敵が1機のみ
                                DoAttack(BoardCells.GetLastAttackPoint(alphaSide)
                                        .Plus(BoardCells.GetLastMoveVector(!alphaSide)));
                                return;
                            } else {
                                // 敵が2機以上
                            }
                        }
                    }
                    break;
                case 1:
                    // 敵を波高しした
                    ArrayList<Point> possibilityPoints = BoardCells
                            .PointRound(BoardCells.GetLastAttackPoint(alphaSide));
                    if (BoardCells.GetLastAttackPoint(!alphaSide) == null) {
                        // 敵が移動しなかった
                        HashMap<Point, Integer> pointsRound = new HashMap<Point, Integer>();
                        for (Point point : possibilityPoints) {
                            if (BoardCells.CanAttackPoint(alphaSide, point)) {
                                pointsRound.put(point, BoardCells.PointRound(point).size());
                            }
                        }
                        Integer maxRound = Collections.max(pointsRound.values());
                        for (Map.Entry<Point, Integer> pointRound : pointsRound.entrySet()) {
                            if (pointRound.getValue() == maxRound) {
                                DoAttack(pointRound.getKey());
                                return;
                            }
                        }
                    } else {
                        // 敵が移動した
                    }
                    break;
                case 0:
                    // 前ターンで敵に命中しなかった

                    break;
            }
        }
        Random random = new Random();
        if (random.nextDouble() <= 1) {
            ArrayList<Point> points = new ArrayList<Point>();
            for (Integer x = 0; x < BoardCells.GetBoardSize(); x++) {
                for (Integer y = 0; y < BoardCells.GetBoardSize(); y++) {
                    if (BoardCells.GetCell(x, y).GetCanAttack(alphaSide)) {
                        points.add(new Point(x, y));
                    }
                }
            }
            DoAttack(points.get(random.nextInt(points.size())));
        }
    }

    private void DoMove(Point oldPoint, Point newPoint) {
        System.out.println(newPoint.Minus(oldPoint) + " に移動！");
        BoardCells.MovePoint(alphaSide, oldPoint, newPoint);
    }

    private void DoAttack(Point point) {
        System.out.println(point + " に魚雷発射！");
        BoardCells.AttackPoint(alphaSide, point);
    }
}

class BattleShip {
    public static final Integer maxTurnCount = 1000;

    public static BoardCells boardCells = new BoardCells();
    public static Algorithm alphAlgorithm = new Algorithm(true);
    public static Algorithm bravoAlgorithm = new Algorithm(false);

    public static void main(String args[]) {
        for (Point point : BoardCells.RandomPoints(4)) {
            BoardCells.GetCell(point).SetHp(true, 3);
        }
        for (Point point : BoardCells.RandomPoints(4)) {
            BoardCells.GetCell(point).SetHp(false, 3);
        }
        boolean alphaSide = true;
        Scanner scanner = new Scanner(System.in);
        while (BoardCells.IsContinue(false)) {
            BoardCells.WriteBoardHp(alphaSide);
            if (alphaSide) {
                alphAlgorithm.Think();
            } else {
                bravoAlgorithm.Think();
            }
            alphaSide = !alphaSide;
            if (BoardCells.GetTurnCount() == maxTurnCount) {
                BoardCells.IsContinue(true);
                break;
            }
        }
        System.out.println("ゲームが終了しました");
        scanner.nextLine();
        scanner.close();
    }

}