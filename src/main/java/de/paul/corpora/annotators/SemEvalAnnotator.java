package de.paul.corpora.annotators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import de.paul.annotations.Annotatable;
import de.paul.annotations.JSONAnnotation;
import de.paul.annotations.annotators.AnnotationException;
import de.paul.annotations.annotators.RESTAnnotator;
import de.paul.annotations.annotators.XLisaAnnotator;
import de.paul.corpora.parsers.LiParser;
import de.paul.corpora.parsers.LiParser.ScoredDocPair;
import de.paul.db.JSONDocSourceLoader;
import de.paul.documents.AnnotatedDoc;
import de.paul.documents.JSONSerializableDocWrapper;
import de.paul.documents.impl.JSONTextDoc;
import de.paul.documents.impl.SemanticallyExpandedDoc;
import de.paul.similarity.docScorers.SemanticallyExpandedDocScorer;
import de.paul.util.Paths;

public class SemEvalAnnotator extends CorpusAnnotator {
	
	private String corpuspath;
	public SemEvalAnnotator (String corpus) throws IOException {
		corpuspath = corpus;
	}
	public static void main(String[] args) throws AnnotationException,
	IOException {

		SemEvalAnnotator annotator = new SemEvalAnnotator(args[0]);
		// add your sentence-pair file
		annotator.annotateSentencePairs(args[0]+"text_input_data/input.txt");
		
	}
	private void annotateSentencePairs(String filename) throws AnnotationException, IOException {
		int id = 0;
		// result object for evaluation file (sim scores)
		TreeMap<Integer, Double> simScoresMap = new TreeMap<Integer, Double>();
		// result object for annotated corpus file
		ArrayList<JSONSerializableDocWrapper> docs = new ArrayList<JSONSerializableDocWrapper>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        int count = 0;
        ArrayList<String> sent1 = new ArrayList<String>();
        ArrayList<String> sent2 = new ArrayList<String>();
        String tmp = "";
        while ((tmp = reader.readLine()) != null) {
                                String[] str = tmp.split("\t");
                                String str1 = str[0];
                                String str2 = str[1];
                                sent1.add(str1);
                                sent2.add(str2);
                        }
        String[] sentences1 = new String[sent1.size()];
        sent1.toArray(sentences1);
        String[] sentences2 = new String[sent2.size()];
        sent2.toArray(sentences2);
        int entities_headlines = 0;
        int original_headlines = 0;
        SemanticallyExpandedDocScorer scorer = new SemanticallyExpandedDocScorer(3, null, null);
        for (int i = 0; i < sent1.size(); i++)
        {
        
                String doc1 = sentences1[i];
                String doc2 = sentences2[i];
                JSONSerializableDocWrapper jsonsent1 = annotateDoc(i,doc1);
                JSONSerializableDocWrapper jsonsent2 = annotateDoc(i,doc2);
               // Sentence-1 -Start
                
                List<JSONAnnotation> annots1 = new LinkedList<JSONAnnotation>();
                String text1 = jsonsent1.getString("text");
				JSONArray ann1 = jsonsent1.getJSONArray("annotations");
				for (int j = 0; j < ann1.length(); j++) {
					JSONObject annj = ann1.getJSONObject(j);
					String ent = annj.getString("entity");
					double w = annj.getDouble("weight");
					annots1.add(new JSONAnnotation(ent, w));
				}
				String idString = Integer.toString(i);
				// Sentence-1 -end 
				AnnotatedDoc sent_annotdoc1 = new JSONTextDoc(text1,"",idString,annots1);
				// Sentence-2 -Start
                
                List<JSONAnnotation> annots2 = new LinkedList<JSONAnnotation>();
                String text2 = jsonsent2.getString("text");
				JSONArray ann2 = jsonsent2.getJSONArray("annotations");
				for (int j = 0; j < ann2.length(); j++) {
					JSONObject annj = ann2.getJSONObject(j);
					String ent = annj.getString("entity");
					double w = annj.getDouble("weight");
					annots2.add(new JSONAnnotation(ent, w));
				}
				// Sentence-2 -end
				AnnotatedDoc sent_annotdoc2 = new JSONTextDoc(text2,"",idString,annots2);
				
                SemanticallyExpandedDoc sent_doc1 = new SemanticallyExpandedDoc(sent_annotdoc1,3,null,null);  
                SemanticallyExpandedDoc sent_doc2 = new SemanticallyExpandedDoc(sent_annotdoc2,3,null,null);
                simScoresMap.put(i,scorer.score(sent_doc1, sent_doc2));
          
        }
		  this.writeMapToCSVFile(simScoresMap, "text_output_data/output.txt");
	}

	private void writeMapToCSVFile(Map<Integer, Double> simScoresMap,
			String momievalpathCsv) {

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(momievalpathCsv));
			for (Map.Entry<Integer, Double> entry : simScoresMap.entrySet()) {
			    bw.write(entry.getValue()+"\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected RESTAnnotator getAnnotator() {
		// TODO Auto-generated method stub
		return new XLisaAnnotator();
	}
}