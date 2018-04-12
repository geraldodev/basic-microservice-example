(ns basic-microservice-example.endpoint-test
  (:require [midje.sweet :refer :all]
            [basic-microservice-example.components :as components]
            [basic-microservice-example.http-helpers :refer [GET POST]]))

(components/ensure-system-up! :test-system)

(fact "we can get user links"
  (GET "/about" 200) => nil)

