# Administrator documentation

## Introduction.
Generate public and private keys on the server<br>
To run `/stamp generate keyPair`<br>

If there is more than one server, this should be copied. <br>
In addition, S3 is used for multiple servers, so configure S3 settings in config.json on each server.

## List of administrative commands
command: `/minestamp advance <abstract stamp> [time] [size] [particle size] [accuracy]`<br>
example: `/minestamp advance :cucumber: 1 3 1.5 32`<br>
description: Generates more advanced stamps.<br>

command: `/minestamp generate keypair`<br>
description: Generate private and public keys.<br>

command: `/minestamp publish roulette`<br>
description: Generate tickets for roulette.<br>

command: `/minestamp publish unique <stamp>`<br>
example: `/minestamp publish unique :cucumber:`<br>
description: Generate a ticket to activate a specific stamp<br>

command: `/minestamp reload`<br>
description: Reloads the settings. Also, if images etc. are added, they will be reflected by executing this command.<br>

## Add language settings

Add a file with the language code in the lang directory. The file name is the language code. The file format is
properties. <br>
example:`zh_CN.properties`, `fr_FR.properties` <br>
defaultSupportedLanguage: `en_US`, `ja_JP` <br>
if you want to add a new language, please create a file with the language code in the lang directory. <br>
original file is [here](https://github.com/Nlkomaru/MineStamp/blob/master/src/main/resources/lang/en_US.properties) <br>


## Configuration file and directory structure

### config.json
Basic configuration. This will always be placed on each server.

```json
{
  "type": "S3",
  "s3Config": {
    "url": "http://minio.example.com:9000/",
    "bucket" : "test",
    "accessKey" : "minio",
    "secretKey" : "minio123"
  },
  "lang" : "en_US"
}
```
### random.json
This is the setting when using roulette tickets. Set the shortCode and the corresponding weight.

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

## For single server
```
└── EmojiStamp/
    ├── lang/
    │   ├── zh_CN.properties
    │   └── fr_FR.properties
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

## For multiple servers
This is a multi-server configuration.

Directory structure on the local server side
```
└── EmojiStamp/
    ├── lang/
    │   ├── zh_CN.properties
    │   └── fr_FR.properties
    ├── config.json
    ├── privateKey
    └── publicKey
```

Directory structure on the s3 side
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




