Millennium ASR Lattice Visualiser
================================================================================
A visualisation tool for word/phone lattices output by the [Millennium ASR system](http://distantspeechrecognition.sourceforge.net/).

* **Version:** 2011-11-28
* **Author:** Todd Shore
* **Website:** https://github.com/errantlinguist/LatticeVisualiser
* **Licensing:** Copyright 2011--2012 Todd Shore. Licensed for distribution under the Apache License 2.0: See the files `NOTICE` and `LICENSE`.

Requirements
--------------------------------------------------------------------------------
- Java SE 6+
- [Apache Commons CLI 1.2+](http://commons.apache.org/cli/)
- [Guava 10.0.1+](http://code.google.com/p/guava-libraries/)
- [Commons-Collections with Generics 4.01+](https://github.com/megamattron/collections-generic)
- The following JARs from the [JUNG Framework 2.0.1+](http://sourceforge.net/projects/jung/):
	- `jung-algorithms-2.0.1.jar`
	- `jung-api-2.0.1.jar`
	- `jung-graph-impl-2.0.1.jar`
	- `jung-visualization-2.0.1.jar`
	
Instructions
--------------------------------------------------------------------------------
- **Building:** Run `ant` in the application home directory, specifying the JAR files required for the above-listed requirements in the property `externaljars`, e.g.

		ant -Dexternaljars="/usr/share/java/guava.jar:/usr/share/java/commons-cli.jar:lib/collections-generic-4.01.jar:lib/jung-algorithms-2.0.1.jar:lib/jung-graph-impl-2.0.1.jar:lib/jung-visualization-2.0.1.jar"

	for a Debian/Ubuntu-style system where the JUNG libraries are locally installed under the directory `./lib`.
- **Running a demo:** Run `ant demo` with required classpath, e.g.

		ant demo -Dexternaljars="/usr/share/java/guava.jar:/usr/share/java/commons-cli.jar:lib/collections-generic-4.01.jar:lib/jung-algorithms-2.0.1.jar:lib/jung-graph-impl-2.0.1.jar:lib/jung-visualization-2.0.1.jar".
- **Generating Javadoc:** Run `ant javadoc` with required classpath.
