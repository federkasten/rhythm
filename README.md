# rhythm

A ClojureScript library handling states and actions for event-driven programming

## Usage

Add the following dependency to your `project.clj`:

```
[rhythm "0.1.1-SNAPSHOT"]
```

### Setup actor

```clj
(let [example-actor (actor "loading-example" {:state [:waiting]})]
    (-> example-actor
        (add-action! :load
                     (fn [f]
                       (switch-state! a :waiting :loading)
                       ;; some heavy processing
                       (js/setTimeout (fn [e] (switch-state! :loading :done)) 10000)))
        (add-state! [:done :error]
                    {:in (fn []
                           (js/setTimeout #(switch-state! a :done :waiting) 100))
                     :out (fn []
                           (.log js/console "Reset actor to waiting"))})))
```

### Watch state

To add watcher

```clj
(watch-state! example-actor :done
    (fn [key]
        (.log js/console "Done!"))

```

To remove watcher

```clj
(unwatch-state! example-actor :done)
```

### Trigger action

```clj
(trigger-action! example-actor :load)
```

## License

Copyright 2014 [Takashi AOKI][federkasten]

Licensed under the [Apache License, Version 2.0][apache-license-2.0].

[federkasten]: http://federkasten.net
[apache-license-2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
