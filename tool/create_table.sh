#!/bin/sh

#
# OfficeFamimaのテーブルを作成するシェル
#
aws dynamodb create-table \
    --table-name OfficeFamima \
    --attribute-definitions \
        AttributeName=Name,AttributeType=S \
        AttributeName=Price,AttributeType=S \
    --key-schema AttributeName=Name,KeyType=HASH AttributeName=Price,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
