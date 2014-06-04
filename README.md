# rhythm

A ClojureScript library handling states and events

# Usage

Setup actor

```clj
(let [example-actor (actor "loading-example" {:state [:waiting]})]
    (-> example-actor
        (add-event! :load
                    (fn [f]
                      (switch-state! a :waiting :loading)
                      ;; some heavy processing
                      (js/setTimeout (fn [e] (switch-state! :loading :done)) 10000)))
        (add-state! :done
                    {:in (fn []
                           (js/setTimeout #(switch-state! a :done :waiting) 100))
                     :out (fn []
                           (.log js/console "Reset actor to waiting"))})
        (add-state! :error
                    {:in (fn []
                           (js/setTimeout #(switch-state! a :done :waiting) 100))
                     :out (fn []
                           (.log js/console "Reset actor to waiting"))})))
```

Watch state changes

```clj
(watch-state-changes! example-actor :done
    (fn [key]
        (.log js/console "Done!"))
```

Trigger event

```clj
(trigger-event! example-actor :load)
```
