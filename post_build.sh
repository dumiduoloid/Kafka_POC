#!/bin/bash

# changing the use case topic name.

sed -i '/    kafka.admin.topic=/c\    kafka.admin.topic=use-case-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/admin-service-ms/admin-service-ms-conf.yaml

sed -i '/    kafka.admin.topic=/c\    kafka.admin.topic=use-case-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/hutchison-mno-connector/hutchison-mno-connector-conf.yaml

sed -i '/    kafka.admin.topic=/c\    kafka.admin.topic=use-case-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/request-coordinator/request-coordinator-conf.yaml

# changing the transaction topic name.

sed -i '/    kafka.transaction.topic=/c\    kafka.transaction.topic=transaction-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/admin-service-ms/admin-service-ms-conf.yaml


sed -i '/    kafka.transaction.topic=/c\    kafka.transaction.topic=transaction-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/hutchison-mno-connector/hutchison-mno-connector-conf.yaml


sed -i '/    kafka.transaction.topic=/c\    kafka.transaction.topic=transaction-topic' /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa/request-coordinator/request-coordinator-conf.yaml

# applying the configuration changes to the cluster.

kubectl apply -k /home/ec2-user/hgc-sre-artifacts/kubernetes-manifests/overlays/qa

if [ $? -eq 0 ]
then
	echo ':::::::::: The post-build script completed successfully ::::::::::'
else
	echo ':::::::::: An error was observed in execution ::::::::::'
	exit 1
fi
