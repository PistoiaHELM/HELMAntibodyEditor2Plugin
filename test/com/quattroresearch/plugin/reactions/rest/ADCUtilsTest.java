/**
 * *****************************************************************************
 * Copyright C 2016, The Pistoia Alliance
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *****************************************************************************
 */
package com.quattroresearch.plugin.reactions.rest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.roche.antibody.model.antibody.Antibody;
import org.roche.antibody.model.antibody.Connection;
import org.roche.antibody.model.antibody.Peptide;
import org.roche.antibody.services.DomainService;
import org.roche.antibody.services.PreferencesDefault;
import org.roche.antibody.services.ProteaseDescription;
import org.roche.plugin.reactions.rest.ADCUtils;
import org.roche.plugin.reactions.rest.DomainDetectionMutationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * {@code ADCUtilsTest} Class to test the ADC-Reactions, the sortase coupling, biotinylation and cleavage.
 * 
 * @author <b>Sabrina Hecht:</b> hecht AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ADCUtilsTest {

	/** The Logger for this class */
	private static final Logger LOG = LoggerFactory.getLogger(ADCUtilsTest.class);

	private static final String TEST_CHAIN = "TESTPESTTESTPESTLPETGGGSGSTESTPESTTESTPEST"
	          // AVI Tag
	          + "GLNDIFEAQKIEWHE";
	
	  public static final ProteaseDescription SORTASE = new ProteaseDescription(1, "Sortase",
		      "^([ARNDCEQGHILKMFPSTWYVX]*?LPET)G[ARNDCEQGHILKMFPSTWYVX]*$", 1, "",
		      "CASE_INSENSITIVE", 0);

	@Test
	public void biotinylationTest() throws Exception {

		PreferencesDefault.USE_MASTER_LIBRARIES = false;
		PreferencesDefault.USE_MASTER_SETTINGS = false;
		Antibody antibody = buildTestAntibodySingleChain();

		ADCUtils.performBiotinylation(antibody);

		Assert.assertEquals(ADCUtils.BIOTIN_COUPLING_DEFAULT_SMILES,
				antibody.getElements().get(1).getSequence().toString());
		Assert.assertEquals(2, antibody.getElements().size());
		HashSet<Connection> connections = antibody.getAllConnections();
		Assert.assertEquals(1, connections.size());
		Iterator it = connections.iterator();
	
		while(it.hasNext()){
			Connection con = (Connection) it.next();

			Assert.assertEquals("Avi-tag:52 <-> Biotin:1", con.toString());
		}

	}

	private Antibody buildTestAntibodySingleChain() throws Exception {
		String testSequence = TEST_CHAIN;
		List<Peptide> peptides = new LinkedList<Peptide>();
		Peptide peptide = new DomainDetectionMutationProcessor().processDomainAndMutationDetection("Test Sequence",
				testSequence);
		peptides.add(peptide);
		Antibody antibody = new Antibody(peptides);
		return antibody;
	}
	
	@Test
	public void sortaseCleavageTest() throws Exception {
		PreferencesDefault.USE_MASTER_LIBRARIES = false;
		PreferencesDefault.USE_MASTER_SETTINGS = false;
	    Antibody antibody = cleaveTestAntibodySingleChain();
	    for (Peptide pep : antibody.getPeptides()) {
	      LOG.debug("Peptide after cleavage: " + pep.getSequence());
	    }
	    Assert.assertEquals("Cleavage should result in 2 peptides", 2, antibody.getPeptides().size());

		
	}
	
	private Antibody cleaveTestAntibodySingleChain() throws Exception {
	    Antibody antibody = buildTestAntibodySingleChain();
	    Assert.assertEquals("Test antibody of wrong size", 1, antibody.getPeptides().size());
	    ADCUtils.cleaveAntibody(antibody, -1, SORTASE);
	    return antibody;
	  }

	
	@Test
	public void sortaseCouplingAntibodyPeptideTest() throws Exception{
		PreferencesDefault.USE_MASTER_LIBRARIES = false;
		PreferencesDefault.USE_MASTER_SETTINGS = false;
		 Antibody antibody = cleaveTestAntibodySingleChain();
		    // we now have an antibody with two unconnected chains. One chain has GGG tag, the other one LPET

		    String peptideToCoupleGGG = "GGGTESTTESTTESTTESTTESTTEST";
		    String peptideToCoupleLPET = "TESTTESTTESTTESTTESTTESTLPET";
		    Peptide peptideGGG =
		        new DomainDetectionMutationProcessor().processDomainAndMutationDetection(DomainService.getInstance().getDefaultName(peptideToCoupleGGG), peptideToCoupleGGG);
		    Peptide peptideLPET =
		        new DomainDetectionMutationProcessor().processDomainAndMutationDetection(DomainService.getInstance().getDefaultName(peptideToCoupleLPET), peptideToCoupleLPET);

		    // test coupling to GGG peptide. Should be appended to antibody chain ending with LPET
		    
		    System.out.println(antibody.getElements().get(0).getSequence());
		    ADCUtils.performSortaseCoupling(antibody, peptideGGG);
		    // outcome: one antibody with 2 chains from which one of it ends with peptideToCoupleGGG
		    Assert.assertEquals(2, antibody.getPeptides().size());
		    boolean isFound = false;
		    for (Peptide pep : antibody.getPeptides()) {
		      LOG.debug(pep.getSequence());
		      if (pep.getSequence().endsWith(peptideToCoupleGGG)) {
		        isFound = true;
		      }
		    }
		    Assert.assertEquals(true, isFound);

		    // test coupling to LPET peptide. Should be prepended to antibody chain starting with GGG
		    ADCUtils.performSortaseCoupling(antibody, peptideLPET);
		    // outcome: one antibody with 2 chains from which one of it starts with peptideToCoupleLPET
		    Assert.assertEquals(2, antibody.getPeptides().size());
		    isFound = false;
		    for (Peptide pep : antibody.getPeptides()) {
		      LOG.debug(pep.getSequence());
		      if (pep.getSequence().startsWith(peptideToCoupleLPET)) {
		        isFound = true;
		      }
		    }
		    Assert.assertTrue( isFound);
	}
	
	


}
