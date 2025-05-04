(ns flickr-aws.adapters.aws-test
  (:require [clojure.test :refer [deftest is testing]]
            [flickr-aws.adapters.aws :as adapters.aws]
            [matcher-combinators.test :refer [match?]]
            [clojure.pprint :as pprint]))

(defn pp [v]
  (pprint/pprint v)
  v)

(deftest ->ddb-batch-put-items-request-test
  (testing "FIXME, I fail."
    (is (match? nil (adapters.aws/->ddb-batch-put-items-request [{:item-key {:partition-key "20250101"
                                                                             :sort-key "1"}
                                                                   :item-values {}}])))))
