## Purpose 

Facilitate either long or short document comparison using efficient graph-based approaches.

## Setup

1. Code is written java. Make sure that all required java dependencies are installed.
2. For similar document search set up ElasticSearch Index.
	* Download ElasticSearch and run bin/elasticsearch.bat
	* Open http://localhost:9200/_plugin/marvel/sense/index.html in browser
	* Enter text from "elasticSearch_index.txt" file to create a new index (if desired, customize index name (default: docIndex) and document type name (default: news))
		* for large collections, increasing the shard count (e.g. 5) will be useful.
		* if necessary, reset with: DELETE docIndex , then create new index.

3. Instantiate class SemExpRelDocSearch with 
	* Path where document triple store (where expanded documents are stored) shall be put. Directory must exist!
	* ElasticSearch index name and document type name
	* Expansion radius of documents (number of edges are traversed in knowledge graph to enrich documents), default: 2 (should not be greater than 3)
	* Candidate Set Size: defines how many candidate documents Pre-Search returns for closer similarity computation in Full Search step. Default: 20 (can be increased at will)

## Use Cases

1. Find related documents for a new, previously unknown document (which will be added to the collection in the process)
	* Call method getRelatedDocuments with query document as AnnotatedDoc.

2. Bulk-expand and -load documents to collection
	* Use method bulkAddDocuments
		
3. Pairwise Document Similarity (without constructing ElasticSearch/Lucene index for inverted-index-lookup - thus slow for related document search)
	* Instantiate class SemanticallyExpandedDocScorer with expansion radius, edge directionality and combination mode for transversal and hierarchical scores (latter two can be null, default will be used)
	* call score() method with two SemanticallyExpandedDocs, which can be a) created out of simple AnnotatedDocs (slower) or b) loaded from document triple store, in case they belong to a collection that has been constructed using the steps explained above for Related Document Search (faster)


## Citation

Christian Paul, Achim Rettinger, Aditya Mogadala, Craig A. Knoblock, Pedro Szekely. In 13th Extended Semantic Web Conference (ESWC). Springer International Publishing. (2016) [Best Paper Candidate]
