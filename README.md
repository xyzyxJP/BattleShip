
# BattleShip

認知科学2021 潜水艦撃沈ゲーム  
RTX 3080 Ti

# Documentation

## ゲームロジック(BattleShip.java)

| boolean | alphaSide |
| ------- | --------- |
| α       | true      |
| β       | false     |

### セル(Cell)

| 変数名           | 変数型     | デフォルト値 |                    |
| :-----------: | :-----: | :----: | :----------------: |
| alphaHp       | int     | -1     | αの戦艦HP(存在しない場合は-1) |
| bravoHp       | int     | -1     | βの戦艦HP(存在しない場合は-1) |
| alphaValue    | int     | 0      | αのアルゴリズム用の評価値      |
| bravoValue    | int     | 0      | βのアルゴリズム用の評価値      |
| alphaIsAttack | boolean | false  | αの攻撃の可否            |
| bravoIsAttack | boolean | false  | βの攻撃の可否            |

| メソッド名       | 引数型              | 戻り値型    |          |                            |     |
| ----------- | ---------------- | ------- | -------- | -------------------------- | --- |
| GetHp       | boolean          | int     | 戦艦HPを取得  |                            |     |
| SetHp       | boolean, int     |         | 戦艦HPを設定  |                            |     |
| GetValue    | boolean          | int     | 評価値を取得   |                            |     |
| SetValue    | boolean, int     |         | 評価値を設定   |                            |     |
| SetIsAttack | boolean, boolean |         | 攻撃の可否を設定 | セルに自軍戦艦がいない場合は`true`を設定できる |     |
| GetIsAttack | boolean, boolean | boolean | 攻撃の可否を取得 |                            |     |
| IsAlive     | boolean          | boolean | 戦艦の生死を取得 |                            |     |
| IsEmpty     | boolean          | boolean | 戦艦の有無を取得 |                            |     |

第1引数には`alphaSide`を割当  

### ポイント(Point)

| 変数名 | 変数型 | デフォルト値 |       |
| --- | --- | ------ | ----- |
| x   | int | 0      | 座標のX値 |
| y   | int | 0      | 座標のY値 |

| メソッド名    | 引数型   | 戻り値型    |            |
| -------- | ----- | ------ | ---------- |
| Plus     | Point | Point  | 引数のポイントを加算 |
| Minus    | Point | Point  | 引数のポイントを減算 |
| toString |       | String | (y, x)     |

### ボード(Board)

| int | attackResult |
| ------- | ------------ |
| 撃沈！     | 3            |
| 命中！     | 2            |
| 波高し！    | 1            |
| ハズレ！    | 0            |

| 変数名                   | 変数型                | デフォルト値 |            |
| --------------------- | ------------------ | ------ | ---------- |
| boardSize             | int                | 5      | ボードの1辺の長さ  |
| cells                 | Cell[][]           |        | セルの2次元配列   |
| turnCount             | int                | 0      | ターン数       |
| alphaWin              | boolean            | false  | αの勝利       |
| bravoWin              | boolean            | false  | βの勝利       |
| lastAlphaAttackPoint  | Point              |        | αの前回攻撃ポイント |
| lastAlphaAttackResult | ArrayList\<Integer\> | -1     | αの前回攻撃結果   |
| lastAlphaMoveVector   | Point              |        | αの前回移動ベクトル |
| lastBravoAttackPoint  | Point              |        | βの前回攻撃ポイント |
| lastBravoAttackResult | ArrayList\<Integer\> | -1     | βの前回攻撃結果   |
| lastBravoMoveVector   | Point              |        | βの前回移動ベクトル |
| visibleLog            | boolean            | false  | ログの表示      |

| メソッド名                | 引数型                          | 戻り値型               |                         |                                       |
| -------------------- | ---------------------------- | ------------------ | ----------------------- | ------------------------------------- |
| Initialize           | boolean                      |                    | 初期化                     | 引数はログの表示する場合のみ`true`にする               |
| WriteBoardHp         | boolean*                     |                    | 戦艦HPを表示                 |                                       |
| WriteBoardIsAttack   | boolean*                     |                    | 攻撃の可否を表示                |                                       |
| WriteLogSide         | boolean                      |                    | ログの表示                   |                                       |
| WriteLogLine         | String                       |                    | ログの表示                   |                                       |
| WriteLog             | String                       |                    | ログの表示                   |                                       |
| IsContinue           | boolean                      | boolean            | ターン続行の可否                | 引数は強制終了する場合のみ`true`にする                |
| GetAlphaWin          |                              | boolean            | αの勝利を取得                 |                                       |
| GetBravoWin          |                              | boolean            | βの勝利を取得                 |                                       |
| SetTurnCount         |                              |                    | ターン数に1加算                |                                       |
| GetTurnCount*        |                              | int                | ターン数を取得                 |                                       |
| GetBoardSize*        |                              | int                | ボードの1辺の長さを取得            |                                       |
| GetCell*             | Point                        | Cell               | ポイントのセルを取得              |                                       |
| GetCell*             | int, int                     | Cell               | ポイントのセルを取得              |                                       |
| SetCell              | Point, Cell                  |                    | ポイントにセルを設定              |                                       |
| SetCell              | int, int, Cell               |                    | ポイントにセルを設定              |                                       |
| IsLastAttack*        | boolean*                     | boolean            | 前回移動の有無を取得              |                                       |
| IsLastMove*          | boolean*                     | boolean            | 前回攻撃の有無を取得              |                                       |
| GetLastAttackPoint*  | boolean*                     | Point              | 前回攻撃ポイントを取得             |                                       |
| GetLastAttackResult* | boolean*                     | ArrayList\<Integer\> | 前回攻撃結果を取得               |                                       |
| GetLastMoveVector*   | boolean*                     | Point              | 前回移動ベクトルを取得             |                                       |
| InitializeValues*    | boolean*                     |                    | 全ての評価値を0に設定             |                                       |
| NormalizeValues*     | boolean*                     |                    | 全ての評価値を周囲4ポイントリストの個数に設定 |                                       |
| GetShipPoints*       | boolean*                     | ArrayList\<Point\> | 戦艦のポイントリストを取得           |                                       |
| GetRandomPoint*      | Point                        | ArrayList\<Point\> | ポイントリストからランダムに取得        |                                       |
| GetRandomPoints*     | int                          | ArrayList\<Point\> | ランダムなポイントリストを取得         |                                       |
| SetRandom4Points     | boolean*                     |                    | ランダムな4ポイントに戦艦を配置        |                                       |
| GetMaxValuePoints*   | boolean*                     | ArrayList\<Point\> | 評価値が最大であるポイントリストを取得     |                                       |
| GetShortPoints*      | boolean*, Point              | ArrayList\<Point\> | ポイントに最も近い戦艦のポイントリストを取得  |                                       |
| GetRoundPoints*      | Point                        | ArrayList\<Point\> | ポイントの周囲8ポイントリストを取得      |                                       |
| GetCrossPoints*      | Point, int                   | ArrayList\<Point\> | ポイントの周囲4ポイントリストを取得      | 第2引数は距離であり、2の場合は移動可能な範囲である            |
| GetPointDistance*    | Point, Point                 | int                | ポイント間の距離                | X軸間の距離 + Y軸間の距離である                    |
| IsMoveEnablePoint*   | boolean*, Point, Point       | boolean            | 移動の可否                   | 第2, 3引数はポイントである                       |
| IsMoveEnableVector*  | boolean*, Point, Point       | boolean            | 移動の可否                   | 第2引数はポイントであり、第3引数はベクトルである             |
| MovePoint            | boolean*, Point, Point       | boolean            | 戦艦の移動                   | 第2, 3引数はポイントである                       |
| MoveVector           | boolean*, Point, Point       | boolean            | 戦艦の移動                   | 第2引数はポイントであり、第3引数はベクトルである             |
| MoveVectorForce      | boolean*, Point              |                    | 戦艦の強制移動                 | 第2引数はベクトルである                          |
| SearchAttackPoints*  | boolean*                     |                    | 攻撃の可否を検索                |                                       |
| GetAttackPoints*     | boolean*                     | ArrayList\<Point\> | 攻撃が可能なポイントリストを取得        |                                       |
| IsAttackPoint*       | boolean*, Point              | boolean            | ポイントの攻撃の可否を取得           |                                       |
| AttackPoint          | boolean*, Point, boolean     | boolean            | ポイントへの攻撃                | 第3引数は攻撃結果をあとから設定する場合のみ`false`にする      |
| AttackResultTransfer | boolean*, ArrayList\<Integer\> |                    | ポイントへの攻撃結果を設定           | `AttackPoint`の第3引数を`false`にした場合のみ使用する |
| AttackPointForce     | boolean*, Point              |                    | ポイントへの強制攻撃              |                                       |

引数型に`*`がついている第1引数は`alphaSide`を割当  
メソッド名に`*`がついているメソッドは`Algorithm`内で使用可能  

### インターフェイス(Interface)

| 変数名           | 変数型     | デフォルト値 |                            |
| ------------- | ------- | ------ | -------------------------- |
| alphaSide     | boolean | false  | `Board`における`alphaSide`     |
| isEnemySecret | boolean | false  | 攻撃結果をあとから設定する場合のみ`true`にする |
| allyCount     | int     | 4      | 自軍戦艦の数                     |
| allySumHp     | int     | 12     | 自軍戦艦の総HP                   |
| enemyCount    | int     | 4      | 敵軍戦艦の数                     |
| enemySumHp    | int     | 12     | 敵軍戦艦の総HP                   |

| メソッド名         | 引数型          | 戻り値型 |            |                 |
| ------------- | ------------ | ---- | ---------- | --------------- |
| DoMove*       | Point, Point |      | 戦艦の移動      | 第1, 2引数はポイントである |
| DoMoveForce   | Point        |      | 戦艦の強制移動    | 第1引数はベクトルである    |
| DoAttack*     | Point        |      | ポイントへの攻撃   |                 |
| DoAttackForce | Point        |      | ポイントへの強制攻撃 |                 |

メソッド名に`*`がついているメソッドのみ`Algorithm`内で一度限り使用可能  

### ロガー(Logger)

| 変数名        | 変数型        | デフォルト値 |     |
| ---------- | ---------- | ------ | --- |
| jsonObject | JSONObject |        |     |

| メソッド名                | 引数型     | 戻り値型                 |               |     |
| -------------------- | ------- | -------------------- | ------------- | --- |
| AddLogger            | boolean |                      | 現在の戦況をJsonに書込 |     |
| GetHpArrayList       | boolean | ArrayList/<Integer/> |               |     |
| GetValueArrayList    | boolean | ArrayList/<Integer/> |               |     |
| GetIsAttackArrayList | boolean | ArrayList/<Boolean/> |               |     |
| SaveLogger           | String  |                      | Jsonを保存       |     |

## AlgorithmXXX.java

### アルゴリズム(Algorithm)の開発

`Algorithm001.java`を参照

```java
import java.util.*;

class AlgorithmXXX extends Interface {
    AlgorithmXXX(boolean alphaSide, boolean isEnemySecret) {
        super(alphaSide, isEnemySecret);
    }

    public void Think() {
        if (Board.IsLastAttack(!alphaSide)) {
            if (Board.GetLastAttackResult(!alphaSide).contains(3)) {
                allySumHp--;
                allyCount--;
            }
            if (Board.GetLastAttackResult(!alphaSide).contains(2)) {
                allySumHp--;
            }
        }
        if (Board.IsLastAttack(alphaSide)) {
            if (Board.GetLastAttackResult(alphaSide).contains(3)) {
                enemySumHp--;
                enemyCount--;
            }
            if (Board.GetLastAttackResult(alphaSide).contains(2)) {
                enemySumHp--;
            }
        }
        Board.SearchAttackPoints(alphaSide);
        /* 
        アルゴリズム
        
        移動する場合
            DoMove(oldPoint, newPoint);
            return;

        攻撃する場合
            DoAttack(point);
            return;

        移動・攻撃したあとにはreturn;を書いてThink()を終了する
        */
        return;
    }
}
```

## DebugPlay.java

2つのアルゴリズムを勝負する  
αとβの初期配置を設定する必要がある  

## ReleasePlay.java

アルゴリズムを実際に使う  
αの初期配置のみ設定する必要がある  

---

[@xyzyxJP](https://twitter.com/xyzyxJP)
