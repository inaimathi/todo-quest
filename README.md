# todo-quest

A Clojure library designed to ... well, that part is up to you.

## Usage

FIXME

```
todo-quest.core> (oauth-access-token consumer-key consumer-secret "9f8d2bb98c096cdf5629" nil)
{:access-token "2587021eb9f884d79cff298fd0705ab02311bc78", :scope nil, :token-type "bearer"}
todo-quest.core> (oauth-authorize consumer-key "http://localhost:3000")
true
todo-quest.core> (oauth-authorize consumer-key nil)
true
todo-quest.core> (oauth-client "2587021eb9f884d79cff298fd0705ab02311bc78")
#function[oauth.v2/wrap-oauth-access-token/fn--7307]
todo-quest.core> request
CompilerException java.lang.RuntimeException: Unable to resolve symbol: request in this context, compiling:(*cider-repl todo-quest*:1:7464)
todo-quest.core> core/request
CompilerException java.lang.RuntimeException: No such namespace: core, compiling:(*cider-repl todo-quest*:1:7464)
todo-quest.core> ((oauth-client "2587021eb9f884d79cff298fd0705ab02311bc78") "https://api.github.com/user")
ClassCastException java.lang.String cannot be cast to clojure.lang.Associative  clojure.lang.RT.assoc (RT.java:792)
todo-quest.core> ((oauth-client "2587021eb9f884d79cff298fd0705ab02311bc78") {:uri "https://api.github.com/user"}})
RuntimeException Unmatched delimiter: }  clojure.lang.Util.runtimeException (Util.java:221)
RuntimeException Unmatched delimiter: )  clojure.lang.Util.runtimeException (Util.java:221)
todo-quest.core> ((oauth-client "2587021eb9f884d79cff298fd0705ab02311bc78") {:uri "https://api.github.com/user"})
NullPointerException   clojure.core/name (core.clj:1546)
todo-quest.core> ((oauth-client "2587021eb9f884d79cff298fd0705ab02311bc78") {:uri "https://api.github.com/user"})
NullPointerException   clojure.core/name (core.clj:1546)
todo-quest.core> ((oauth-client {:access-token "2587021eb9f884d79cff298fd0705ab02311bc78", :scope nil, :token-type "bearer"}) {:uri "https://api.github.com/user"})
NullPointerException   clojure.core/name (core.clj:1546)
todo-quest.core> (oauth-client {:access-token "2587021eb9f884d79cff298fd0705ab02311bc78", :scope nil, :token-type "bearer"})
#function[oauth.v2/wrap-oauth-access-token/fn--7307]
todo-quest.core> (doc (oauth-client {:access-token "2587021eb9f884d79cff298fd0705ab02311bc78", :scope nil, :token-type "bearer"}))
ClassCastException clojure.lang.PersistentList cannot be cast to clojure.lang.Symbol  clojure.core/find-ns (core.clj:3996)
todo-quest.core> (oauth-client {:access-token "2587021eb9f884d79cff298fd0705ab02311bc78", :scope nil, :token-type "bearer"})
#function[oauth.v2/wrap-oauth-access-token/fn--7307]
todo-quest.core>
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
