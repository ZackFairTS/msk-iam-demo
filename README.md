# msk-iam-demo
Authenticate to Amazon MSK (Managed Streaming for Kafka) using IAM authentication 

## Overview
This project demonstrates how to authenticate to Amazon MSK (Managed Streaming for Kafka) using IAM authentication and send/receive messages.

## Prerequisites

1. Java 11 or higher
2. Maven
3. AWS CLI configured with appropriate IAM permissions
4. An Amazon MSK cluster with IAM authentication enabled
5. IAM permissions to access the MSK cluster

## Required IAM Permissions

The IAM user or role needs the following permissions:
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "kafka-cluster:Connect",
                "kafka-cluster:AlterCluster",
                "kafka-cluster:DescribeCluster"
            ],
            "Resource": [
                "arn:aws:kafka:region:account-id:cluster/cluster-name/*"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "kafka-cluster:*Topic*",
                "kafka-cluster:WriteData",
                "kafka-cluster:ReadData"
            ],
            "Resource": [
                "arn:aws:kafka:region:account-id:topic/cluster-name/*"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "kafka-cluster:AlterGroup",
                "kafka-cluster:DescribeGroup"
            ],
            "Resource": [
                "arn:aws:kafka:region:account-id:group/cluster-name/*"
            ]
        }
    ]
}
```

Replace `region`, `account-id`, and `cluster-name` with your specific values.

## Building the Project

```bash
mvn clean package
```

This will create a JAR file with all dependencies in the `target` directory.

## Running the Producer

```bash
java -cp target/msk-iam-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.mskiam.MSKProducer <bootstrap-servers> <topic-name> [number-of-messages]
```

Example:
```bash
java -cp target/msk-iam-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.mskiam.MSKProducer b-1.mycluster.abcdef.c2.kafka.us-east-1.amazonaws.com:9098,b-2.mycluster.abcdef.c2.kafka.us-east-1.amazonaws.com:9098 my-topic 20
```

## Running the Consumer

```bash
java -cp target/msk-iam-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.mskiam.MSKConsumer <bootstrap-servers> <topic-name> <consumer-group-id>
```

Example:
```bash
java -cp target/msk-iam-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.mskiam.MSKConsumer b-1.mycluster.abcdef.c2.kafka.us-east-1.amazonaws.com:9098,b-2.mycluster.abcdef.c2.kafka.us-east-1.amazonaws.com:9098 my-topic my-consumer-group
```

## Notes

1. Make sure your AWS credentials are properly configured in your environment.
2. The bootstrap servers should use port 9098 for IAM authentication.
3. The topic must exist before running the producer/consumer.
