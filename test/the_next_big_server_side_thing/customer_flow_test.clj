(ns the-next-big-server-side-thing.customer-flow-test
  (:require [midje.sweet :refer :all]
            [postman.flow :refer [*world* flow]]
            [the-next-big-server-side-thing.aux.init :as aux.init]
            [the-next-big-server-side-thing.components.mock-http :as mock-http]
            [the-next-big-server-side-thing.http-helpers :refer [GET POST]]))

(defn get-customer! [id world]
  (mock-http/with-responses
    ["http://customer-service.com/by-name/bob" {:body {:name "bob"}}]
    (assoc world
           :customer-http-resp
           (GET "/customer" 200))))

(defn create-account! [customer-id world]
  (assoc world :created-account nil))

(defn account-balance [account-id world]
  (assoc world :account-balance 0))

(defn delete-account! [account-id world]
  world)

(defn get-account-from-customer-id [customer-id world]
  (assoc world :account nil))

(def bob-id 1)

(flow "create savings account and check balance"
  aux.init/init!
  aux.init/restart!

  (partial get-account-from-customer-id bob-id)

  (fact "There shouldn't be a savings account for bob yet"
    (:account *world*) => nil)

  create-account!

  (partial get-account-from-customer-id bob-id)

  (fact "There should now be a savings account for bob"
    (:account *world*) => nil)


  (partial delete-account! bob-id)

  (partial get-account-from-customer-id bob-id)

  (fact "After deleting the savings account it can no longer be found"
    (:account *world*) => nil)
  )

;; logging at each step
;; retry logic baked in
;; offers some measure of uniformity and re-use
