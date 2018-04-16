(ns basic-microservice-example.logic-test
  (:require [midje.sweet :refer :all]
            [basic-microservice-example.logic :as logic])
  (:import [java.util UUID]))

(def customer-id (UUID/randomUUID))

(fact "New account generation"
  (logic/new-account customer-id "Abel") => (just {:id          uuid?
                                                   :name        "Abel"
                                                   :customer-id customer-id}))
