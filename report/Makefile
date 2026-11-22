# Kompileerib aruande Dockeri texlive konteineris: make  /  make clean
IMAGE = texlive/texlive:latest
ROOT  = $(abspath ..)
RUN   = docker run --rm -u $$(id -u):$$(id -g) -v $(ROOT):/work -w /work/report $(IMAGE)

aruanne.pdf: aruanne.tex tiitelleht.tex ../src/main/java/GraphTask.java ../src/test/java/GraphTaskTest.java
	$(RUN) latexmk -pdf -interaction=nonstopmode -halt-on-error aruanne.tex

clean:
	$(RUN) latexmk -C aruanne.tex

.PHONY: clean
