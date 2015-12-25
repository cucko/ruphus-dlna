## Flags table ##
|80000000|31|`senderPaced`|
|:-------|:-|:------------|
|40000000|30|`lsopTimeBasedSeekSupported`|
|20000000|29|`lsopByteBasedSeekSupported`|
|10000000|28|`playcontainerSupported`|
| 8000000|27|`s0IncreasingSupported`|
| 4000000|26|`sNIncreasingSupported`|
| 2000000|25|`rtspPauseSupported`|
| 1000000|24|`streamingTransferModeSupported`|
|  800000|23|`interactiveTransferModeSupported`|
|  400000|22|`backgroundTransferModeSupported`|
|  200000|21|`connectionStallingSupported`|
|  100000|20|`dlnaVersion15Supported`|

Padded with 24 trailing 0s

## Example ##
`(1 << 24) | (1 << 22) | (1 << 21) | (1 << 20)`
```
DLNA.ORG_FLAGS=01700000[000000000000000000000000]
```
_`[]`_ show padding