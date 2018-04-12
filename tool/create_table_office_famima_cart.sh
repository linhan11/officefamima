#!/bin/sh

#
# OfficeFamimaのテーブルを作成するシェル
#
aws dynamodb create-table \
    --table-name OfficeFamimaCart \
    --attribute-definitions \
        AttributeName=ID,AttributeType=S \
    --key-schema AttributeName=ID,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
