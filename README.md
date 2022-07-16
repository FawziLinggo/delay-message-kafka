# How To Delay Message on Kafka?

The consumer API is centered around the ```poll()``` method, which is used to retrieve records from the brokers. 
The ```subscribe()``` method controls which topics will be fetched in poll. Typically, consumer usage involves an 
initial call to ```subscribe()``` to setup the topics of interest and then a loop which calls ```poll()``` until 
the application is shut down.

Using Asynchronous commits at offset in topic partition.
```consumer.commitAsync();```

and use the API Function :
```consumer.seek(topicPartition, record.offset());```

Kafka allows specifying the position using ```{seek(TopicPartition, long)}``` to specify the new position. Special 
methods for seeking to the earliest and latest offset the server maintains are also available ```(#seekToBeginning(Collection)``` 
and (```seekToEnd(Collection)``` respectively).

Finally we have to make an algorithm where each message timestamp must be compared with the current timestamp if it is greater than the delay time then the message will be delayed.