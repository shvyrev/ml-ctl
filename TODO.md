## Allocated Resources

```shell
$ kubectl get node ml-1n5.cp.dev.cldx.ru -o json | jq '.status.allocatable'                   ml-1n5-admin@ml-1n5-cluster
{
  "cpu": "32",
  "ephemeral-storage": "1901355124456",
  "hugepages-1Gi": "0",
  "hugepages-2Mi": "0",
  "memory": "160855292Ki",
  "nvidia.com/gpu": "7",
  "pods": "110"
}
```