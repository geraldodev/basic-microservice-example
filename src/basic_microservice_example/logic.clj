(ns basic-microservice-example.logic
  (:import [java.util UUID]))

(defn new-account [customer-id customer-name]
  {:id          (UUID/randomUUID)
   :name        customer-name
   :customer-id customer-id})
