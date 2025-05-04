(ns flickr-aws.ports.aws
  (:require [cognitect.aws.client.api :as aws.api]
            [flickr-aws.adapters.aws :as adapters.aws]
            [flickr-aws.constants.aws :as constants.aws]
            [clojure.pprint :as pprint]))

#_(defn ^:private ssm-parameter-value
  [ssm-client parameter-name parameter-type]
  (-> (aws.api/invoke ssm-client
                      {:op :GetParameter
                       :request
                       {:Name (parameter-name constants.flickr/ssm-parameters-names)
                        :Type (parameter-type constants.aws/ssm-parameter-types)}})
      :Parameter
      :Value))

(defn s3-get-object
  [s3-client request]
  (aws.api/invoke s3-client
                  {:op :GetObject
                   :request request}))

(defn s3-delete-object
  [s3-client request]
  (aws.api/invoke s3-client
                  {:op :DeleteObject
                   :request request}))

(defn ddb-get-item
  [ddb-client request]
  (some-> (aws.api/invoke ddb-client
                          {:op :GetItem
                           :request request})))

(defn ddb-update-item
  [ddb-client request]
  (some-> (aws.api/invoke ddb-client
                          {:op :UpdateItem
                           :request request})))

(defn ddb-put-item
  [ddb-client request]
  (some-> (aws.api/invoke ddb-client
                          {:op :PutItem
                           :request request})))

(defn ddb-batch-put-items
  [ddb-client request]
  (some-> (aws.api/invoke ddb-client
                          {:op :BatchWriteItem
                           :request request})))

#_(defn ssm-flickr-credentials
    [ssm-client]
    (let [flickr-key (ssm-parameter-value ssm-client :param-key :string)
          flickr-secret (ssm-parameter-value ssm-client :param-secret :string)
          flickr-oauth-token (ssm-parameter-value ssm-client :param-token :string)
          flickr-oauth-token-secret (ssm-parameter-value ssm-client :param-token-secret :string)]
      (adapters.flickr/ssm-parameters->flickr-credentials flickr-key
                                                          flickr-secret
                                                          flickr-oauth-token
                                                          flickr-oauth-token-secret)))

#_(defn s3-image-as-input-stream
  [s3-client object-key]
  (let [{content-type :ContentType
         body         :Body} (some->> object-key
                                      (adapters.aws/->s3-object-request constants.flickr/s3-bucket)
                                      (s3-get-object s3-client))]
    (when (= content-type "image/jpeg")
      body)))

#_(defn s3-delete-image
  [s3-client object-key]
  (some->> (adapters.aws/->s3-object-request constants.flickr/s3-bucket object-key)
           (s3-delete-object s3-client)))

#_(defn ddb-flickr-get-photo-info
    [ddb-client item-key]
    (some->> (adapters.aws/->ddb-get-photo-info-request item-key)
             (ddb-get-item ddb-client)
             :Item
             (adapters.flickr/ddb-item->flickr-photo-info)))

(defn ddb-flickr-update-photo-id
  [ddb-client item-key update-values]
  (some->> (adapters.aws/->ddb-update-photo-id-request item-key update-values)
           (ddb-update-item ddb-client)))

(defn ddb-flickr-put-item
  [ddb-client item]
  (some->> (adapters.aws/->ddb-put-item-request item)
           (ddb-put-item ddb-client)))

(defn ddb-flickr-batch-put-items
  [ddb-client items]
  (some->> (adapters.aws/->ddb-batch-put-items-request items)
           (ddb-batch-put-items ddb-client)))
