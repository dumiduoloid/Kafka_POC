#!/bin/bash

# changing the use case topic name.

sed -i '/    kafka.admin.topic=use-case-topic/c\    kafka.admin.topic=test-uc-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/admin-service-ms/admin-service-ms-conf.yaml

sed -i '/    kafka.admin.topic=use-case-topic/c\    kafka.admin.topic=test-uc-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/hutchison-mno-connector/hutchison-mno-connector-conf.yaml

sed -i '/    kafka.admin.topic=use-case-topic/c\    kafka.admin.topic=test-uc-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/request-coordinator/request-coordinator-conf.yaml

# changing the transaction topic name.

sed -i '/    kafka.transaction.topic=transaction-topic/c\    kafka.transaction.topic=test-transaction-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/admin-service-ms/admin-service-ms-conf.yaml


sed -i '/    kafka.transaction.topic=transaction-topic/c\    kafka.transaction.topic=test-transaction-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/hutchison-mno-connector/hutchison-mno-connector-conf.yaml


sed -i '/    kafka.transaction.topic=transaction-topic/c\    kafka.transaction.topic=test-transaction-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/request-coordinator/request-coordinator-conf.yaml

# applying the configuration changes to the cluster.

kubectl apply -k /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa

if [ $? -eq 0 ]
then
	echo ':::::::::: The pre-build script completed successfully ::::::::::'
else
	echo ':::::::::: An error was observed in execution ::::::::::'
	exit 1
fi
