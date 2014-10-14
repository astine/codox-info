# codox-info

A plugin for the codox documenation generator that generates .info files.

## Usage

Add this code to your `project.clj`

    :plugins [[codox "0.8.10"]
              [codox-info "0.1.1-SNAPSHOT"]]
    :codox {:writer codox-info.core/write-to-info}

and an .info file be generated in the `doc` directory of your project. You can install this file with the `install-info` program.
    
## License

Copyright © 2014 Andrew Stine

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
