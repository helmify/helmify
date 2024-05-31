#!/bin/sh

ls -lah

ls -lah /chart

helm dependency update

helm lint .