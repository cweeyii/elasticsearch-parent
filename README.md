# elasticsearch-parent
elasticsearch集群的搜索功能包装：client包见elasticsearch-client-1.0-snapshot.jar
可以运行的任务见：elasticsearch-task: ElasticSearchConditionTest 和 ElasticBasicJobTest (需要更换数据库和配置ES mapping)
es集群安装见：http://blog.csdn.net/cweeyii/article/details/71055884
程序中mapping如下：
POST /enterprise_info_index
{
   "settings": {
      "index": {
         "number_of_replicas": "1",
         "number_of_shards": "5"
      }
   },
   "mappings": {
      "enterprise_basic_info": {
         "_all": {
            "enabled": false
         },
         "properties": {
            "id": {
               "type": "long"
            },       
            "enterpriseName": {
               "type": "string",
               "analyzer": "ik_max_word"
            },
            "address": {
               "type": "string",
               "analyzer": "ik_max_word"
            },
            "latitude": {
               "type": "double"
            },
            "longitude": {
               "type": "double"
            },
            "phone": {
               "type": "string",
               "index": "not_analyzed"
            },
            "businessCategory": {
               "type": "string",
               "index": "not_analyzed"
            },
            "cityName": {
               "type": "string",
               "index": "not_analyzed"
            },
            "districtName": {
               "type": "string",
               "index": "not_analyzed"
            },
            "valid": {
               "type": "long"
            },
            "location": {
               "type": "geo_point",
                                "geohash_prefix":true,
               "geohash_precision":12
            }
         }
      }
   }
}
