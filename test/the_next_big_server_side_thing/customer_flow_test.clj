(ns the-next-big-server-side-thing.customer-flow-test
  (:require [midje.sweet :refer :all]
            [postman.flow :refer [*world* flow]]
            [the-next-big-server-side-thing.aux.init :as aux.init]
            [the-next-big-server-side-thing.components.mock-http :as mock-http]
            [the-next-big-server-side-thing.http-helpers :refer [GET POST]]))

(defn get-customer! [world]
  (mock-http/with-responses
    ["http://customer-service.com/by-name/bob" {:body {:name "bob"}}]
    (assoc world
           :customer-http-resp
           (GET "/customer" 200))))

(flow "check service version"
  aux.init/init!
  aux.init/restart!

  get-customer!

  (fact "we can get user links"
    (:customer-http-resp *world*) => nil))
