(ns the-next-big-server-side-thing.customer-flow-test
  (:require [midje.sweet :refer :all]
            [postman.flow :refer [*world* flow]]
            [the-next-big-server-side-thing.aux.init :as aux.init]
            [the-next-big-server-side-thing.http-helpers :refer [GET POST]]))

(flow "check service version"
  aux.init/init!
  ;aux.init/restart!

  (fact "we can get user links"
    (GET "/customer" 200) => nil))
