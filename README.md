To test:

```
java -Dhostname=host2.com -Dpostwait=6 -DchunkLevel=0 ClientChunk
```

with:
```
<VirtualHost *:8000>
    ServerName host1.com
    ErrorLog "logs/error_log.host1.com"
    RequestReadTimeout header=5 body=5
</VirtualHost>
<VirtualHost *:8000>
    ServerName host2.com
    ErrorLog "logs/error_log.host2.com"
    RequestReadTimeout header=5 body=10
</VirtualHost>
```

java -Dhostname=host2.com -Dpostwait=4 -DchunkLevel=0 ClientChunk

java -Dhostname=host1.com -Dpostwait=4 -DchunkLevel=0 ClientChunk

Both should give 404

java -Dhostname=host2.com -Dpostwait=6 -DchunkLevel=0 ClientChunk

java -Dhostname=host1.com -Dpostwait=6 -DchunkLevel=0 ClientChunk

First one should give 404, second will give 408.
