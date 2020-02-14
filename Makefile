BASE_NAME=index

PROJECT_DIR=$(CURDIR)/src/docs
OUTPUT_DIR=$(CURDIR)/target/docs
SEQDIAG_GENERATOR=podman run -it -v $(PROJECT_DIR):/documents:Z -v $(OUTPUT_DIR):/output:Z -w /documents/ quay.io/jaeichle/asciidoctor-redhat-fonts seqdiag
ASCIIDOCTOR_COMMAND=podman run -it -v $(PROJECT_DIR):/documents:Z -v $(OUTPUT_DIR):/output:Z -w /documents/ quay.io/jaeichle/asciidoctor-redhat-fonts asciidoctor
ASCIIDOCTOR_PDF_COMMAND=podman run -it -v $(PROJECT_DIR):/documents:Z -v $(OUTPUT_DIR):/output:Z -w /documents/ quay.io/jaeichle/asciidoctor-redhat-fonts asciidoctor-pdf
ASCIIDOCTOR_PARAMS=-r asciidoctor-diagram

SPELL = hunspell
SPELLOPTS = -d en_GB

all: maven $(BASE_NAME).pdf $(BASE_NAME).html $(BASE_NAME).xml

seqdiag: 01-uma-flow-access.seqdiag.png 01-uma-flow-access-with-token.seqdiag.png 01-uma-register-resource.seqdiag.png

clean:
	rm -rf $(OUTPUT_DIR)
	rm -rf $(PROJECT_DIR)/generated
	mvn clean

prepare: clean
	mkdir -p $(PROJECT_DIR)/generated
	mkdir -p $(OUTPUT_DIR)/generated

%.seqdiag.png: prepare
	$(SEQDIAG_GENERATOR) $*.seqdiag -o generated/$*.seqdiag.png

spell:
	$(SPELL) $(SPELLOPTS) $(PROJECT_DIR)/*.adoc

maven:
	mvn install

# https://github.com/asciidoctor/asciidoctor-pdf
%.pdf: prepare seqdiag
	$(ASCIIDOCTOR_PDF_COMMAND) $(ASCIIDOCTOR_PARAMS) $*.adoc -o /output/$*.pdf

%.xml: prepare seqdiag
	$(ASCIIDOCTOR_COMMAND) $(ASCIIDOCTOR_PARAMS) -b docbook5 $*.adoc -o /output/$*.xml

%.html: prepare seqdiag
	cp $(PROJECT_DIR)/generated/*.png $(OUTPUT_DIR)/generated
	$(ASCIIDOCTOR_COMMAND) $(ASCIIDOCTOR_PARAMS) -b html $*.adoc -o /output/$*.html
