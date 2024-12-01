# 管理者向けドキュメント

## はじめに
サーバーで公開鍵と秘密鍵を生成します。<br>
`/stamp generate keyPair` を実行します。

その後、複数サーバーの場合にはこれをコピーします。
また複数サーバーの場合S3を使用するため、各サーバーのconfig.jsonにてS3の設定を行います。

## 管理用コマンド一覧
command: `/minestamp advance <abstract stamp> [time] [size] [particle size] [accuracy]`<br>
demo: `/minestamp advance :cucumber: 1 3 1.5 32`<br>
description: より高度なスタンプを生成します<br>

command: `/minestamp generate keypair`<br>
description: 秘密鍵と公開鍵を生成します<br>

command: `/minestamp publish roulette`<br>
description: ルーレット用のチケットを生成します<br>

command: `/minestamp publish unique <stamp>`<br>
demo: `/minestamp publish unique :cucumber:`<br>
description: 特定のスタンプを有効化するためのチケットを生成します<br>

command: `/minestamp reload`<br>
description: 設定を再読み込みします また、画像等を追加した場合もこのコマンドを実行することで反映されます<br>

## コンフィグ一覧

### config.json
基本的な設定です。 これは必ず各サーバーに置かれることになります。

```json
{
  "type": "S3",
  "s3Config": {
    "url": "http://minio.example.com:9000/",
    "bucket" : "test",
    "accessKey" : "minio",
    "secretKey" : "minio123"
  }
}
```
### random.json
ランダムチケットを使った際の設定です。 shortCodeとそれに対応する重みを設定します。

```json
{
  ":last-track-button:": 1,
  ":hourglass-not-done:": 1,
  ":man-cook:-light-skin-tone:": 1,
  ":man-detective:-dark-skin-tone:": 1,
  ":person:-medium-skin-tone--blond-hair:": 1,
  ":clapping-hands:-light-skin-tone:": 1,
  ":cricket-game:": 1
}
```

### player-default.json
```json
{
  "second": 1,
  "accuracy": 32,
  "size": 3,
  "particleSize": 1.5,
  "defaultEmoji": [
    ":cucumber:",
    ":thinking-face:",
    ":angry-face:",
    ":sleeping-face:"
  ]
}
```

## シングルサーバー用
```
└── EmojiStamp/
    ├── image/
    │   ├── picture-1.png
    │   └── picture-2.jpg
    ├── player/
    │   ├── aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa.json
    │   └── ffffffff-ffff-ffff-ffff-ffffffffffff.json
    ├── config.json
    ├── player-default.json
    ├── random.json
    ├── privateKey
    └── publicKey
```

## Proxy用
こちらは複数サーバー用の設定です
```
└── EmojiStamp/
    ├── config.json
    ├── privateKey
    └── publicKey
```
minio側
```
├── image/
│   ├── picture-1.png
│   └── picture-2.jpg
├── player/
│   ├── aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa.json
│   └── ffffffff-ffff-ffff-ffff-ffffffffffff.json
├── random.json
└── player-default.json
```




