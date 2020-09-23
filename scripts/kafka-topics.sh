#!/bin/sh

KAFKA_TOPICS="ch.guengel.funnel.persist.envelope ch.guengel.funnel.delete.envelope ch.guengel.funnel.all.envelopes ch.guengel.funnel.envelopes.update"

kubectl -n funnel exec -i deployment/kafka -- sh <<EOF
kafka_host=`hostname`
cd /kafka/bin
for t in $KAFKA_TOPICS
do
  ./kafka-topics.sh --bootstrap-server "$kafka_host":9092 \
    --create \
    --topic "\$t" \
    --partitions 1 \
    --replication-factor 1
  ./kafka-configs.sh --bootstrap-server "$kafka_host":9092 \
    --entity-type topics \
    --entity-name "\$t" \
    --alter \
    --add-config cleanup.policy=delete
  ./kafka-configs.sh --bootstrap-server "$kafka_host":9092 \
    --entity-type topics \
    --entity-name "\$t" \
    --alter \
    --add-config retention.ms=300000
done
EOF
