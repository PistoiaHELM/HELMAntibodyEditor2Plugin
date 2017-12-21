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

import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.roche.antibody.model.antibody.Domain;
import org.roche.antibody.model.antibody.DomainLibraryValues;
import org.roche.antibody.model.antibody.Peptide;
import org.roche.antibody.services.ConfigFileService;
import org.roche.antibody.services.ConfigLoaderAutoconnectorConfig;
import org.roche.antibody.services.ConfigLoaderMutationLibrary;
import org.roche.antibody.services.ConfigLoaderSettings;
import org.roche.antibody.services.DomainDetectionSettingsService;
import org.roche.antibody.services.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quattroresearch.antibody.DomainDetection.E_ErrorType;
import com.quattroresearch.antibody.DomainDetectionStandalone;
import com.quattroresearch.antibody.FindMutations;
import com.quattroresearch.blastws.LocalConfig;

/**
 * 
 * {@code DomainDetectionMutationProcessor} contains various overloads for
 * performing domain detection.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com,
 *         Roche Pharma Research and Early Development - Informatics, Roche
 *         Innovation Center Munich
 * @author <b>Jutta Fichtner:</b> fichtner AT quattro-research DOT com, quattro
 *         research GmbH
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro
 *         research GmbH
 * @version $Id$
 */
public class DomainDetectionMutationProcessor {
	/** The Logger for this class */
	private static final Logger LOG = LoggerFactory
			.getLogger(DomainDetectionMutationProcessor.class);

	private static List<DomainLibraryValues> cachedDomainLibrary;

	private static List<DomainLibraryValues> cachedAntigenLibrary;

	public Peptide processDomainAndMutationDetection(String chainName,
			String chainSequence) throws Exception {
		return processDomainAndMutationDetection(chainName, chainSequence, null);
	}

	public Peptide processDomainAndMutationDetection(String chainName,
			String chainSequence, Connection connection) throws Exception {
		List<Peptide> peptides = processDomainAndMutationDetection(
				Arrays.asList(new String[] { chainName }),
				Arrays.asList(new String[] { chainSequence }), connection);
		if (peptides.size() != 1) {
			throw new Exception(
					"The domain detection has retrieved more than one peptide!");
		}
		return peptides.get(0);
	}

	public List<Peptide> processDomainAndMutationDetection(
			List<String> chainNames, List<String> chainSequences)
			throws Exception {
		return processDomainAndMutationDetection(chainNames, chainSequences,
				null);
	}

	public List<Peptide> processDomainAndMutationDetection(
			List<String> chainNames, List<String> chainSequences,
			Connection connection) throws Exception {
		DomainDetectionStandalone ddObject = runDomainDetection(chainNames,
				chainSequences, false, connection);

		List<Peptide> domainDetectionResults = ddObject
				.calculatePeptides(false);

		// Uses primary loader set on top of the method to search mutations
		FindMutations.find(domainDetectionResults, ConfigFileService
				.getInstance().fetchMutationLibrary());

		return domainDetectionResults;
	}

	public DomainDetectionStandalone runDomainDetection(
			List<String> chainNames, List<String> chainSequences,
			boolean isAntigenDomains) throws Exception {
		return runDomainDetection(chainNames, chainSequences, isAntigenDomains,
				null);
	}

	/**
	 * Runs the domain detection on given input and returns it for retrieval of
	 * result data.
	 * 
	 * @param chainNames
	 *            name of the antibody chains
	 * @param chainSequences
	 *            sequences of the antibody chains
	 * @param isAntigenDomains
	 *            whether only antigen domains or general domains are searched.
	 * @return {@code DomainDetectionStandalone} for result retrieval
	 * @throws Exception
	 */
	private DomainDetectionStandalone runDomainDetection(
			List<String> chainNames, List<String> chainSequences,
			boolean isAntigenDomains, Connection connection) throws Exception {
		LOG.debug("DomainDetectionMutationProcessor running domain detection....");
    try {
      DomainDetectionSettingsService.getInstance()
          .setPrimaryDomainDetectionSettingsLoader(
              new ConfigLoaderSettings());
    } catch (Exception e) {
      LOG.error("Loading domain detection settings failed!", e);
      throw e;
    }
		ConfigFileService
				.getInstance()
				.setPrimaryAutoconnectorConfigLoader(
						new ConfigLoaderAutoconnectorConfig(
								PreferencesService
										.getInstance()
										.getApplicationPrefs()
										.getString(
												PreferencesService.CONFIG_LOADER_JDBC),
								PreferencesService
										.getInstance()
										.getApplicationPrefs()
										.getString(
												PreferencesService.CONFIG_LOADER_URL),
								null));
		ConfigFileService
				.getInstance()
				.setPrimaryMutationLibraryLoader(
						new ConfigLoaderMutationLibrary(
								PreferencesService
										.getInstance()
										.getApplicationPrefs()
										.getString(
												PreferencesService.CONFIG_LOADER_JDBC),
								PreferencesService
										.getInstance()
										.getApplicationPrefs()
										.getString(
												PreferencesService.CONFIG_LOADER_URL),
								null));

		// Ensure only one thread in parallel is creating the DB. Else, the
		// result will be rubbish (Thread-safety!)
		synchronized (LocalConfig.getInstance().getMakeBlastDb()) {

			DomainDetectionStandalone domainDetection = new DomainDetectionStandalone(
					chainNames, chainSequences,
					isAntigenDomains ? cachedAntigenLibrary
							: cachedDomainLibrary);

			if (!domainDetection.makeBlastDatabases()
					.equals(E_ErrorType.NO_ERR)) {
				LOG.error("Unable to create BLAST database.");
				throw new RuntimeException(
						"Domain Detection failed because no BLAST database could be established.");
			} else {
				domainDetection.loadData();
			}

			return domainDetection;
		}
	}

	public List<Domain> detectHitDomainsAboveThreshold(String chainName,
			String chainSequence, boolean isAntigenDomains,
			double aboveIdentityPerCent) throws Exception {
		return detectHitDomainsAboveThreshold(chainName, chainSequence,
				isAntigenDomains, aboveIdentityPerCent, null);
	}

	/**
	 * Returns all general /antigen domains (depends on the isAntigenDomain
	 * parameter), whose threshold lies above the given aboveIdentiyPerCent
	 * threshold
	 * 
	 * @param chainName
	 *            the name of the chain
	 * @param chainSequence
	 *            the sequenc of the chain
	 * @param isAntigenDomains
	 *            true, if antigen domains should be detected, false if only
	 *            general domains should be detected
	 * @param aboveIdentityPerCent
	 *            the threshold, where the domains should lie above
	 * @return
	 * @throws Exception
	 */
	public List<Domain> detectHitDomainsAboveThreshold(String chainName,
			String chainSequence, boolean isAntigenDomains,
			double aboveIdentityPerCent, Connection connection)
			throws Exception {
		List<Domain> hitDomains = new LinkedList<Domain>();
		DomainDetectionStandalone ddObject = runDomainDetection(
				Arrays.asList(new String[] { chainName }),
				Arrays.asList(new String[] { chainSequence }),
				isAntigenDomains, connection);
		hitDomains = ddObject
				.findAllHitDomainsAboveThreshold(aboveIdentityPerCent);
		return hitDomains;
	}

	/**
	 * Re-Annotates given domain. So it performs a blast and adds the best hit
	 * for given domain.
	 * 
	 * @param domain
	 *            domain to annotate
	 * @param peptideSequence
	 *            peptideSequence of given domain
	 * @throws Exception
	 */
	public void annotateDomain(Domain domain, String peptideSequence)
			throws Exception {

		List<String> chainSequences = new LinkedList<>();
		List<String> chainNames = new LinkedList<>();
		chainSequences.add(domain.getSequence());
		chainNames.add(domain.getName());
		DomainDetectionStandalone domainDetection = runDomainDetection(
				chainNames, chainSequences, false);
		domainDetection.annotateDomain(domain, peptideSequence);

	}

}
