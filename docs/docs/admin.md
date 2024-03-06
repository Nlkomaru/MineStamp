# 管理者向けドキュメント

## はじめに
サーバーで公開鍵と秘密鍵を生成します。<br>
`/stamp generate keyPair` を実行します。

その後、複数サーバーの場合にはこれをコピーします。
また複数サーバーの場合、各サーバーのconfig.jsonにてS3の設定を行います。

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




