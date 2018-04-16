(ns basic-microservice-example.controller-test
  (:require [midje.sweet :refer :all]
            [basic-microservice-example.db.saving-account :as db.saving-account]
            [basic-microservice-example.controller :as controller])
  (:import [java.util UUID]))

(def customer-id (UUID/randomUUID))

(fact "Sketching account creation"
  (controller/create-account! customer-id ..storage.. ..http..) => (just {:id          uuid?
                                                                          :name        "Abel"
                                                                          :customer-id customer-id})
  (provided
    (controller/get-customer customer-id ..http..) => {:customer-name "Abel"}
    (db.saving-account/add-account! (contains {:name "Abel"}) ..storage..) => irrelevant))
