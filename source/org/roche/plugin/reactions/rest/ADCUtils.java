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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.roche.antibody.model.antibody.Antibody;
import org.roche.antibody.model.antibody.ChemElement;
import org.roche.antibody.model.antibody.Connection;
import org.roche.antibody.model.antibody.Domain;
import org.roche.antibody.model.antibody.GeneralConnection;
import org.roche.antibody.model.antibody.Peptide;
import org.roche.antibody.services.DomainService;
import org.roche.antibody.services.ProteaseDescription;
import org.roche.antibody.services.helmnotation.HELM;
import org.roche.antibody.services.tools.ProteaseCleavageResult;
import org.roche.antibody.services.tools.ProteaseTools;
import org.roche.plugin.file.InvalidInputException;
import org.roche.plugin.reactions.models.ReactiveMolecule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code ADCUtils} contains various helper functions concerning ADC reactions.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com,
 *         Roche Pharma Research and Early Development - Informatics, Roche
 *         Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro
 *         research GmbH
 * @author <b>Sabrina Hecht:</b> hecht AT quattro-research DOT com, quattro
 *         research GmbH
 * 
 * @version $Id$
 */
public class ADCUtils {

	/** The Logger for this class */
	private static final Logger LOG = LoggerFactory.getLogger(ADCUtils.class);

	public static final String BIOTIN_COUPLING_DEFAULT_SMILES = "[H][C@]12CS[C@@H](CCCCC([*])=O)[C@@]1([H])NC(=O)N2 |$;;;;;;;;;;_R1;;;;;;;$|";

	public static final String BIOTIN_COUPLING_DEFAULT_MOL = "\n"
			+ "  Marvin  12021516302D\n"
			+ "\n"
			+ " 18 19  0  0  0  0            999 V2000\n"
			+ "   11.4932   -8.6180    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"
			+ "   11.4932   -9.4572    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"
			+ "   12.2532   -8.3330    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"
			+ "   10.6540   -9.6920    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   10.2001   -9.0613    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   10.6699   -8.3831    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    9.4560   -9.0613    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   12.2532   -9.6920    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   12.7098   -9.0217    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   12.4512   -7.5941    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.2349   -7.4410    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.4882   -6.6387    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.3011   -6.4672    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.5544   -5.6623    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.0134   -5.0448    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   15.3540   -5.4828    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   11.4932  -10.2990    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   11.4932   -7.7603    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  1  2  1  0  0  0  0\n" + "  1  3  1  0  0  0  0\n"
			+ "  2  4  1  0  0  0  0\n" + "  4  5  1  0  0  0  0\n"
			+ "  1  6  1  0  0  0  0\n" + "  6  5  1  0  0  0  0\n"
			+ "  5  7  2  0  0  0  0\n" + "  2  8  1  0  0  0  0\n"
			+ "  3  9  1  0  0  0  0\n" + "  8  9  1  0  0  0  0\n"
			+ "  3 10  1  6  0  0  0\n" + " 10 11  1  0  0  0  0\n"
			+ " 11 12  1  0  0  0  0\n" + " 12 13  1  0  0  0  0\n"
			+ " 13 14  1  0  0  0  0\n" + " 14 15  1  0  0  0  0\n"
			+ " 14 16  2  0  0  0  0\n" + "  2 17  1  1  0  0  0\n"
			+ "  1 18  1  1  0  0  0\n" + "M  END";

	public static final String ETHYLENE_COUPLING_DEFAULT_SMILES = "[*]OCC[*] |$_R1;;;;_R2$|";

	public static final String ETHYLENE_COUPLING_DEFAULT_MOL = "\n"
			+ "  Marvin  11240913582D\n"
			+ "\n"
			+ " 5  4  0  0  0  0            999 V2000\n"
			+ "  -2.2386    0.1159    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  -1.5241   -0.2966    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  -2.9531   -0.2966    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  -0.8097    0.1159    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  -0.0952   -0.2966    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  1  2  1  0  0  0  0\n" + "  1  3  1  0  0  0  0\n"
			+ "  2  4  1  0  0  0  0\n" + " 4  5  1  0  0  0  0\n"
			+ "  M  RGP  2   3   1   5   2\n" + "M  END";

	public static final String MALEIIMID_COUPLING_DEFAULT_SMILES = "CC[C@H](C)[C@@H]1NC(=O)CNC(=O)C2Cc3c([nH]c4cc(OCc5ccc(NC(=O)[C@H](CCCNC(N)=O)NC(=O)[C@@H](NC(=O)CCCCCN6C(=O)CC([*])C6=O)C(C)C)cc5)ccc34)[S+]([O-])CC(NC(=O)CNC1=O)C(=O)N[C@@H](CC(N)=O)C(=O)N1C[C@H](O)C[C@H]1C(=O)N[C@@H]([C@@H](C)[C@@H](O)CO)C(=O)N2 |$;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;_R1;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;$|";

	public static final String MALEIIMID_COUPLING_DEFAULT_MOL = "\n"
			+ "  Marvin  09151515172D\n"
			+ "\n"
			+ " 105111  0  0  0  0            999 V2000\n"
			+ "   13.9054   -8.3253    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.5679   -7.8343    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.3077   -7.0503    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"
			+ "   13.4802   -7.0503    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.2358   -7.8459    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"
			+ "   12.4541   -8.1085    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.3077   -6.2256    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   15.0281   -5.8134    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   15.5342   -5.1622    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"
			+ "   15.2233   -4.3984    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"
			+ "   15.7292   -3.7471    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.4063   -4.2857    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"
			+ "   14.0953   -3.5219    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.6013   -2.8707    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.9003   -4.9370    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.3512   -5.2748    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.6621   -6.0387    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.8572   -4.6236    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   17.6736   -4.7371    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.3504   -4.2660    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.0968   -4.6167    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.7738   -4.1457    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   20.5202   -4.4963    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   21.1971   -4.0252    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   20.5897   -5.3181    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   21.3355   -5.6686    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"
			+ "   20.9702   -6.4080    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   20.1472   -6.4613    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   21.4284   -7.0940    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   21.2888   -7.9068    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   20.5151   -8.1923    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.9977   -9.0199    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.7253   -8.8497    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.3734   -7.8374    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.1951   -6.6260    0.0000 S   0  3  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.6936   -6.7876    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.4625   -7.6039    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   17.6381   -7.6039    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   17.3650   -6.8298    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.0113   -6.3224    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.0113   -5.4989    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.5597   -6.6805    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.0267   -7.3045    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.3046   -8.0802    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   17.1092   -8.2259    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   15.9130   -8.5511    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   14.7226   -9.5204    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.8524   -9.6466    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.5459  -10.4142    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   12.7304  -10.5318    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   12.2185   -9.8825    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   12.5217   -9.1181    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.3385   -8.9948    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   11.4202  -10.0965    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   11.2063  -10.8948    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   10.4079  -11.1087    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"
			+ "   10.1940  -11.9070    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   10.7785  -12.4914    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   10.5646  -13.2897    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   11.1490  -13.8741    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   10.9351  -14.6724    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   11.5194  -15.2568    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   10.1368  -14.8863    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    9.8236  -10.5243    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    9.0253  -10.7382    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    8.4409  -10.1538    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"
			+ "    8.6548   -9.3555    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    8.0704   -8.7711    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    9.4531   -9.1416    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    7.6426  -10.3677    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    7.0582   -9.7833    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    6.2599   -9.9972    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    5.6755   -9.4128    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    4.8772   -9.6267    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    4.2927   -9.0424    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    3.4944   -9.2562    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    2.9100   -8.6718    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    2.0942   -8.8010    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    1.7191   -8.0650    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    2.3032   -7.4808    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    3.0393   -7.8560    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    3.7550   -7.4427    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    1.6810   -9.5168    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    7.2721   -8.9850    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    8.8113  -11.5365    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   11.7906  -11.4792    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.3738   -5.6477    0.0000 O   0  5  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.5270   -9.6502    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   17.7346   -9.8787    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   17.0496  -10.3397    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"
			+ "   16.3122   -9.9781    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   15.6245  -10.4367    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   17.1058  -11.1624    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.4212  -11.6224    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   16.4773  -12.4452    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   15.6806  -11.2595    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.1211  -10.2221    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   19.8810   -7.6651    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   22.0131   -5.1977    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"
			+ "   22.7595   -5.5483    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   23.4363   -5.0773    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   21.9435   -4.3759    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   18.2809   -3.4443    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   13.5979   -5.8108    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    1.9977   -6.3404    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  1  2  1  0  0  0  0\n" + "  2  3  1  0  0  0  0\n"
			+ "  3  4  1  1  0  0  0\n" + "  4  5  1  0  0  0  0\n"
			+ "  5  1  1  0  0  0  0\n" + "  5  6  1  6  0  0  0\n"
			+ "  3  7  1  0  0  0  0\n" + "  7  8  1  0  0  0  0\n"
			+ "  9  8  1  1  0  0  0\n" + "  9 10  1  0  0  0  0\n"
			+ " 10 11  1  6  0  0  0\n" + " 10 12  1  0  0  0  0\n"
			+ " 12 13  1  0  0  0  0\n" + " 13 14  1  0  0  0  0\n"
			+ " 12 15  1  1  0  0  0\n" + "  9 16  1  0  0  0  0\n"
			+ " 16 17  2  0  0  0  0\n" + " 16 18  1  0  0  0  0\n"
			+ " 18 19  1  0  0  0  0\n" + " 19 20  1  0  0  0  0\n"
			+ " 20 21  1  0  0  0  0\n" + " 21 22  1  0  0  0  0\n"
			+ " 22 23  1  0  0  0  0\n" + " 23 24  2  0  0  0  0\n"
			+ " 23 25  1  0  0  0  0\n" + " 26 25  1  1  0  0  0\n"
			+ " 26 27  1  0  0  0  0\n" + " 27 28  2  0  0  0  0\n"
			+ " 27 29  1  0  0  0  0\n" + " 29 30  1  0  0  0  0\n"
			+ " 30 31  1  0  0  0  0\n" + " 31 32  1  0  0  0  0\n"
			+ " 33 32  1  0  0  0  0\n" + " 34 33  1  6  0  0  0\n"
			+ " 35 34  1  0  0  0  0\n" + " 36 35  1  0  0  0  0\n"
			+ " 37 36  1  0  0  0  0\n" + " 38 37  1  0  0  0  0\n"
			+ " 38 39  2  0  0  0  0\n" + " 40 39  1  0  0  0  0\n"
			+ " 36 40  2  0  0  0  0\n" + " 41 40  1  0  0  0  0\n"
			+ " 41 19  1  1  0  0  0\n" + " 39 42  1  0  0  0  0\n"
			+ " 42 43  2  0  0  0  0\n" + " 43 44  1  0  0  0  0\n"
			+ " 44 45  2  0  0  0  0\n" + " 45 38  1  0  0  0  0\n"
			+ " 44 46  1  0  0  0  0\n" + " 46 47  1  0  0  0  0\n"
			+ " 47 48  1  0  0  0  0\n" + " 49 48  2  0  0  0  0\n"
			+ " 50 49  1  0  0  0  0\n" + " 51 50  2  0  0  0  0\n"
			+ " 52 51  1  0  0  0  0\n" + " 53 52  2  0  0  0  0\n"
			+ " 48 53  1  0  0  0  0\n" + " 51 54  1  0  0  0  0\n"
			+ " 54 55  1  0  0  0  0\n" + " 55 56  1  0  0  0  0\n"
			+ " 56 57  1  6  0  0  0\n" + " 57 58  1  0  0  0  0\n"
			+ " 58 59  1  0  0  0  0\n" + " 59 60  1  0  0  0  0\n"
			+ " 60 61  1  0  0  0  0\n" + " 61 62  2  0  0  0  0\n"
			+ " 61 63  1  0  0  0  0\n" + " 56 64  1  0  0  0  0\n"
			+ " 64 65  1  0  0  0  0\n" + " 65 66  1  0  0  0  0\n"
			+ " 66 67  1  1  0  0  0\n" + " 67 68  1  0  0  0  0\n"
			+ " 67 69  1  0  0  0  0\n" + " 66 70  1  0  0  0  0\n"
			+ " 70 71  1  0  0  0  0\n" + " 71 72  1  0  0  0  0\n"
			+ " 72 73  1  0  0  0  0\n" + " 73 74  1  0  0  0  0\n"
			+ " 74 75  1  0  0  0  0\n" + " 75 76  1  0  0  0  0\n"
			+ " 76 77  1  0  0  0  0\n" + " 78 77  1  0  0  0  0\n"
			+ " 78 79  1  0  0  0  0\n" + " 79 80  1  0  0  0  0\n"
			+ " 80 81  1  0  0  0  0\n" + " 81 77  1  0  0  0  0\n"
			+ " 81 82  2  0  0  0  0\n" + " 78 83  2  0  0  0  0\n"
			+ " 71 84  2  0  0  0  0\n" + " 65 85  2  0  0  0  0\n"
			+ " 55 86  2  0  0  0  0\n" + " 35 87  1  0  0  0  0\n"
			+ " 33 88  1  0  0  0  0\n" + " 88 89  1  0  0  0  0\n"
			+ " 89 90  1  0  0  0  0\n" + " 90 91  1  0  0  0  0\n"
			+ "  2 91  1  0  0  0  0\n" + " 91 92  2  0  0  0  0\n"
			+ " 90 93  1  1  0  0  0\n" + " 93 94  1  0  0  0  0\n"
			+ " 94 95  1  0  0  0  0\n" + " 94 96  2  0  0  0  0\n"
			+ " 88 97  2  0  0  0  0\n" + " 31 98  2  0  0  0  0\n"
			+ " 26 99  1  0  0  0  0\n" + " 99100  1  0  0  0  0\n"
			+ " 100101  1  0  0  0  0\n" + " 99102  1  1  0  0  0\n"
			+ " 20103  2  0  0  0  0\n" + "  7104  2  0  0  0  0\n"
			+ " 80105  1  0  0  0  0\n" + "M  CHG  2  35   1  87  -1\n"
			+ "M  RGP  1 105   1\n" + "M  END";

	public static enum CouplingType {
		NTerm, CTerm, Position
	}

	// only Utils, no instantiation needed
	private ADCUtils() {
	}

	public static void performSortaseCoupling(Antibody antibody, Peptide peptide)
			throws Exception {

		if (sequenceMatchesMotif(peptide.getSequence(),
				"^GGG([ARNDCEQGHILKMFPSTWYVX])*$")) {
			if (antibodyMatchesMotif(antibody,
					"([ARNDCEQGHILKMFPSTWYVX])*LPET$")) {
				LOG.debug("Sortase coupling antibodyLPET to GGGpeptide");
				attachPeptideCTerm(antibody, peptide,
						"([ARNDCEQGHILKMFPSTWYVX])*LPET$");
			}
		}
		if (sequenceMatchesMotif(peptide.getSequence(),
				"^([ARNDCEQGHILKMFPSTWYVX])*LPET$")) {
			if (antibodyMatchesMotif(antibody,
					"^GGG([ARNDCEQGHILKMFPSTWYVX])*$")) {
				LOG.debug("Sortase coupling peptideLPET to GGGantibody.");

				attachPeptideNTerm(antibody, peptide,
						"^GGG([ARNDCEQGHILKMFPSTWYVX])*$");
			}
		}
	}

	private static void attachPeptideCTerm(Antibody antibody, Peptide peptide,
			String antibodyMotif) {
		for (Peptide pep : antibody.getPeptides()) {
			Pattern pattern = Pattern.compile(antibodyMotif,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(pep.getSequence());
			if (matcher.matches()) {
				LOG.debug("Motif found. Adding peptide " + pep.getName()
						+ " c terminally to antibody.");
				DomainService.getInstance().addAsLastPeptide(peptide, pep);
			}
		}
	}

	private static void attachPeptideNTerm(Antibody antibody, Peptide peptide,
			String antibodyMotif) {
		for (Peptide pep : antibody.getPeptides()) {
			Pattern pattern = Pattern.compile(antibodyMotif,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(pep.getSequence());
			if (matcher.matches()) {
				LOG.debug("Motif found. Adding peptide " + pep.getName()
						+ " n-terminally to antibody.");
				DomainService.getInstance().addAsFirstPeptide(peptide, pep);
			}
		}
	}

	private static boolean antibodyMatchesMotif(Antibody antibody, String motif) {
		for (Peptide pep : antibody.getPeptides()) {

			System.out.println(pep.getSequence());
			System.out.println(motif);
			System.out.println(sequenceMatchesMotif(pep.getSequence(), motif));
			if (sequenceMatchesMotif(pep.getSequence(), motif)) {
				return true;
			}
		}
		return false;
	}

	private static boolean sequenceMatchesMotif(String sequence, String motif) {
		Pattern pattern = Pattern.compile(motif, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sequence);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	private static void couplePeptideToMonomer(Peptide peptide,
			ReactiveMolecule monomer, CouplingType couplingType,
			Integer positionOnPeptide) throws InvalidInputException {
		int position = -1;
		String rgroup = HELM.R3;
		if (couplingType == CouplingType.CTerm) {
			position = peptide.getSequence().length();
			rgroup = HELM.R2;
		} else if (couplingType == CouplingType.NTerm) {
			position = 1;
			rgroup = HELM.R1;
		} else if (couplingType == CouplingType.Position) {
			if (positionOnPeptide == null) {
				throw new InvalidInputException(
						"Position is mandatory for coupling type 'Position'!");
			}
			position = positionOnPeptide;
		}

		if (position > 0) {
			Antibody ab = peptide.getAntibody();
			List<ChemElement> chemElements = ab.getChemElements();
			ChemElement chemElement = new ChemElement(ab,
					monomer.getExtendedSmiles(), monomer.getExtendedSmiles(),
					monomer.getMolfile(), monomer.getR1(), monomer.getR2(),
					monomer.getR3());
			chemElement.setName(monomer.getName());
			chemElements.add(chemElement);
			Connection connection = null;
			connection = new GeneralConnection(peptide, chemElement, position,
					1, rgroup, HELM.R1);
			ab.addConnection(connection);
		}
	}

	/**
	 * Searches an AVI tag inside the given antibody and couples its Lysine to
	 * Biotin.
	 * 
	 * @param antibody
	 * @throws InvalidInputException
	 */
	public static void performBiotinylation(Antibody antibody)
			throws InvalidInputException {
		ReactiveMolecule biotin = new ReactiveMolecule(
				BIOTIN_COUPLING_DEFAULT_MOL, "Biotin",
				BIOTIN_COUPLING_DEFAULT_SMILES, null, null, null, "", "",
				"Biotin");

		boolean foundAVI = false;
		for (Peptide pep : antibody.getPeptides()) {
			for (Domain dom : pep.getDomains()) {
				if (dom.getName().equalsIgnoreCase("avi-tag")) {
					int lysinePosition = (dom.getSequence().toUpperCase())
							.indexOf('K') + dom.getStartPosition();
					if (lysinePosition >= 0) {
						couplePeptideToMonomer(pep, biotin,
								CouplingType.Position, lysinePosition);
						foundAVI = true;
					} else {
						LOG.warn("Lysine Position not found in AVI tag when trying to perform biotinylation. ("
								+ dom.getSequence() + ")");
					}
				}
			}
		}

		if (!foundAVI) {
			LOG.warn("Biotinylation could not be performed due to missing AVI-TAG.");
			throw new InvalidInputException(
					"Biotinylation could not be performed due to missing AVI-TAG.");
		}

	}

	public static void cleaveAntibody(Antibody antibody, int index,
			ProteaseDescription protease) throws Exception {
		List<Peptide> retainedPeptides = new LinkedList<Peptide>();
		for (int i = 0; i < antibody.getPeptides().size(); i++) {
			Peptide peptide = antibody.getPeptides().get(i);
			retainedPeptides.add(peptide);
			if (i == index || index < 0) {
				ProteaseCleavageResult result = ProteaseTools
						.doProteaseCleavage(peptide.getSequence(), protease);
				String modifiedSequence = result.getCleavageResult();

				if (!modifiedSequence.equals(peptide.getSequence())) {
					modifySequence(peptide, result);
					if (result.getAppendedAfter() != null
							&& result.getAppendedAfter().length() > 0) {
						DomainService.getInstance().addAsLastDomain(
								result.getAppendedAfter(), peptide);
					}
					retainedPeptides.addAll(retainPreAndPostPeptide(peptide,
							result));
				}
			}
		}

		antibody.setPeptides(retainedPeptides);

	}

	/**
	 * method to retain the preCut sequence and the postCut sequence as peptides
	 * to the same antibody as the cut sequence
	 * 
	 * @param peptide
	 *            Peptide to cut
	 * @param result
	 *            ProteaseCleavageResult
	 * @throws Exception
	 */
	private static List<Peptide> retainPreAndPostPeptide(Peptide peptide,
			ProteaseCleavageResult result) throws Exception {
		List<Peptide> retainedPeptides = new LinkedList<>();
		List<String> names = new LinkedList<String>();
		List<String> sequences = new LinkedList<String>();
		/* Fetch preCut sequence and postCut sequence */
		String sequencePre = result.getLeftCuttedSequence();
		if (sequencePre != null) {
			names.add("Pre");
			sequences.add(sequencePre);
			LOG.debug("Pre sequence to retain.");
		}
		String sequencePost = result.getRightCuttedSequence();
		if (sequencePost != null) {
			names.add("Post");
			sequences.add(sequencePost);
			LOG.debug("Post sequence to retain.");
		}

		/*
		 * re-blast sequences 1. and 2 when length > 0 + add them to the same
		 * antibody
		 */
		if (names.size() > 0) {
			retainedPeptides.addAll(new DomainDetectionMutationProcessor()
					.processDomainAndMutationDetection(names, sequences));
		}

		return retainedPeptides;
	}

	private static void modifySequence(Peptide peptide,
			ProteaseCleavageResult cleavageResult) throws Exception {

		// Sort domains to simplify cutting
		List<Domain> domains = peptide.getDomains();
		Collections.sort(domains, new Comparator<Domain>() {
			@Override
			public int compare(Domain left, Domain right) {
				return left.getStartPosition() - right.getStartPosition();
			}
		});

		int rightCutPos = cleavageResult.getRightCut() + 1;
		int leftCutPos = cleavageResult.getLeftCut() + 1;
		for (int i = domains.size() - 1; i >= 0; i--) {
			if (leftCutPos > domains.get(i).getEndPosition()) {
				DomainService.getInstance().deleteDomain(domains.get(i));
			} else if (leftCutPos > domains.get(i).getStartPosition()
					&& leftCutPos <= domains.get(i).getEndPosition()) {
				/* re-annotate domain */

				DomainService.getInstance().changeDomainSequence(
						domains.get(i),
						domains.get(i)
								.getSequence()
								.substring(
										leftCutPos
												- domains.get(i)
														.getStartPosition()));

				new DomainDetectionMutationProcessor().annotateDomain(
						domains.get(i), peptide.getSequence());

			}
			if (rightCutPos <= domains.get(i).getStartPosition()) {
				DomainService.getInstance().deleteDomain(domains.get(i));
			} else if (rightCutPos > domains.get(i).getStartPosition()
					&& rightCutPos <= domains.get(i).getEndPosition()) {
				/* re-annotate domain */

				DomainService.getInstance().changeDomainSequence(
						domains.get(i),
						domains.get(i)
								.getSequence()
								.substring(
										0,
										rightCutPos
												- domains.get(i)
														.getStartPosition()));

				new DomainDetectionMutationProcessor().annotateDomain(
						domains.get(i), peptide.getSequence());

			}

		}

	}

	public static Antibody performPeptideCleavageAndSortaseCoupling(
			Antibody cleavedAntibody, Peptide peptide,
			ProteaseDescription proteaseDescription, CouplingType couplingType)
			throws Exception {
		/* unmarshal antibody */
		peptide = getCleavedPeptideForSortase(peptide, couplingType,
				proteaseDescription);
		/* perform sortase coupling */
		ADCUtils.performSortaseCoupling(cleavedAntibody, peptide);
		LOG.debug("Sortase coupling returned antibody with "
				+ cleavedAntibody.getElements().size() + " elements.");
		return cleavedAntibody;
	}

	private static Peptide getCleavedPeptideForSortase(Peptide peptide,
			CouplingType couplingType, ProteaseDescription proteaseDescription)
			throws Exception {
		/* perform peptide cleavage */
		if (couplingType == CouplingType.CTerm) {
			LOG.debug("PeptideSequence: " + peptide.getSequence());
			if (!peptide.getSequence().startsWith("GGG")) {
				Antibody addpeptide = new Antibody();
				addpeptide.setPeptides(new ArrayList<Peptide>());
				addpeptide.getPeptides().add(peptide);
				cleaveAntibody(addpeptide, -1, proteaseDescription);
				if (addpeptide.getPeptides().size() != 2) {
					throw new InvalidInputException(
							"Peptide does not contain exactly one cleavage site. Please check input.");
				}
				peptide = addpeptide.getPeptides().get(1);
			}
		}

		else if (couplingType == CouplingType.NTerm) {
			LOG.debug("PeptideSequence: " + peptide.getSequence());
			if (!peptide.getSequence().endsWith("LPET")) {
				Antibody addpeptide = new Antibody();
				addpeptide.setPeptides(new ArrayList<Peptide>());
				addpeptide.getPeptides().add(peptide);
				cleaveAntibody(addpeptide, -1, proteaseDescription);
				if (addpeptide.getPeptides().size() != 2) {
					throw new InvalidInputException(
							"Peptide does not contain exactly one cleavage site. Please check input.");
				}
				peptide = addpeptide.getPeptides().get(0);
			}
		} else {
			throw new InvalidInputException("CouplingType is not known");
		}

		return peptide;
	}

}
