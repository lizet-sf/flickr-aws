(ns flickr-aws.adapters.aws
  (:require [clojure.pprint :as pprint]))

(defn ->ddb-update-photo-id-request
  [{:keys [partition-key sort-key]} & values]
  (when-let [{:keys [photoid]} values]
    {:TableName "flickr-uploads-v01"
     :Key {"PostAt" {:S partition-key}
           "Sequence" {:N sort-key}}
     :UpdateExpression "SET PhotoId = :photoid"
     :ExpressionAttributeValues {":photoid" {:S photoid}}}))

(defn ^:private kv
  [k v]
  (when v {k v}))

(defn ^:private assoc-if
  [m k v]
  (if v (assoc m k v) m))

(defn ->ddb-get-photo-info-request
  [{:keys [partition-key sort-key]}]
  {:TableName "flickr-uploads-v01"
   :ProjectionExpression "PhotoName, Title, Description, Tags"
   :Key {"PostAt" {:S partition-key}
         "Sequence" {:N sort-key}}})

(defn ->s3-object-request
  [s3-bucket object-key]
  {:Bucket s3-bucket
   :Key object-key})

(defn ->ddb-item
  [{:keys [partition-key sort-key]}
   {:keys [photo-name title description tags]}]
  (-> {"PostAt"   {:S partition-key}
       "Sequence" {:N sort-key}}
      (assoc-if "PhotoName"   (kv :S photo-name))
      (assoc-if "Title"       (kv :S title))
      (assoc-if "Description" (kv :S description))
      (assoc-if "Tags"        (kv :S tags))))

(defn ->ddb-put-item-request
  [{:keys [item-key item-values]}]
  {:TableName "flickr-uploads-v01"
   :Item (->ddb-item item-key item-values)})

(defn ->batch-put-item
  [{:keys [item-key item-values]}]
  {:PutRequest {:Item (->ddb-item item-key item-values)}})

(defn ->ddb-batch-put-items-request
  [items]
  (let [ddb-items (mapv ->batch-put-item items)]
    {:RequestItems {"flickr-uploads-v01" ddb-items}}))

