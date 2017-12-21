/*******************************************************************************
 * Copyright C 2016, Roche pREDi (Roche Innovation Center Munich)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package org.roche.plugin.reactions.rest;

import org.roche.antibody.model.antibody.Antibody;
import org.roche.antibody.model.antibody.Peptide;
import org.roche.antibody.services.DomainService;
import org.roche.antibody.services.ProteaseDescription;
import org.roche.plugin.file.InvalidInputException;
import org.roche.plugin.reactions.rest.ADCUtils.CouplingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code ADCReactions} performs cleaving and coupling reactions.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com,
 *         Roche Pharma Research and Early Development - Informatics, Roche
 *         Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro
 *         research GmbH
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro
 *         research GmbH
 * @version $Id$
 */
public class ADCReactions {

	/** The Logger for this class */

	private static final Logger LOG = LoggerFactory
			.getLogger(ADCReactions.class);

	private ADCReactions() {

	}

	public static Antibody biotinylation(Antibody antibody) throws Exception {
		ADCUtils.performBiotinylation(antibody);
		return antibody;
	}

	private static CouplingType defineCouplingType(String couplingType1)
			throws InvalidInputException {
		CouplingType couplingType = null;
		if (couplingType1.equalsIgnoreCase("C")) {
			couplingType = CouplingType.CTerm;
		} else if (couplingType1.equalsIgnoreCase("N")) {
			couplingType = CouplingType.NTerm;
		} else {
			throw new InvalidInputException("Coupling Type '" + couplingType1
					+ "' is unknown!");
		}

		return couplingType;

	}

	public static Antibody cleave(Antibody antibody, int index,
			ProteaseDescription protease) throws Exception {

		ADCUtils.cleaveAntibody(antibody, index, protease);

		return antibody;
	}

	public static Antibody sortaseCouplingPeptide(Antibody cleavedAntibody,
			String peptideInput, String couplingType1,
			ProteaseDescription sortase) throws Exception {
		Antibody result = new Antibody();
		CouplingType couplingType = defineCouplingType(couplingType1);
		if (cleavedAntibody == null && peptideInput == null) {
			throw new InvalidInputException(
					"Antibody and molecule are mandatory for coupling reactions!");
		} else if (peptideInput != null) {

			// coupling antibody + peptide
			Peptide peptide = new DomainDetectionMutationProcessor()
					.processDomainAndMutationDetection(DomainService
							.getInstance().getDefaultName(peptideInput),
							peptideInput);
			result = ADCUtils.performPeptideCleavageAndSortaseCoupling(
					cleavedAntibody, peptide, sortase, couplingType);

			LOG.debug("Sortase coupling returned antibody with "
					+ result.getElements().size() + " elements.");
		}
		return result;
	}

}
