#!/bin/sh

if [ $# -ne 1 ]; then
  echo "$0 <container_name>"
  exit 1
fi

KAFKA_TOPICS="ch.guengel.funnel.rest.feedenvelope.deletion ch.guengel.funnel.rest.feedenvelope.new ch.guengel.funnel.retriever.feedenvelope.notify ch.guengel.funnel.retriever.feedenvelope.update"

docker exec -i "$1" sh <<EOF
cd /opt/bitnami/kafka/bin
for t in $KAFKA_TOPICS
do
  ./kafka-topics.sh --bootstrap-server 127.0.0.1:9092 \
    --create \
    --topic "\$t" \
    --partitions 10 \
    --replication-factor 1
  ./kafka-configs.sh --bootstrap-server 127.0.0.1:9092 \
    --entity-type topics \
    --entity-name "\$t" \
    --alter \
    --add-config cleanup.policy=compact
  ./kafka-configs.sh --bootstrap-server 127.0.0.1:9092 \
    --entity-type topics \
    --entity-name "\$t" \
    --alter \
    --add-config max.compaction.lag.ms=600000
  ./kafka-configs.sh --bootstrap-server 127.0.0.1:9092 \
    --entity-type topics \
    --entity-name "\$t" \
    --alter \
    --add-config min.compaction.lag.ms=300000
done
EOF
