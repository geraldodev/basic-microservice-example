(ns basic-microservice-example.service-test
  (:require [midje.sweet :refer :all]
            [matcher-combinators.midje :refer [match]]
            [basic-microservice-example.components :as components]
            [basic-microservice-example.http-helpers :refer [GET POST]]))

(components/ensure-system-up! :test-system)

(fact "hitting home page endpoint"
  (GET "/" 200) => (match {:body {:message "Hello World!"}}))

