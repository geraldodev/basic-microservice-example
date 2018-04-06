(ns the-next-big-server-side-thing.endpoint-test
  (:require [midje.sweet :refer :all]
            [the-next-big-server-side-thing.components :as components]
            [the-next-big-server-side-thing.http-helpers :refer [GET POST]]))

(components/ensure-system-up! :test-system)

(fact "we can get user links"
  (GET "/about" 200) => nil)
