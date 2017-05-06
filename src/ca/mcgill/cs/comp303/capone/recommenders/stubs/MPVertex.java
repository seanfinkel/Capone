package ca.mcgill.cs.comp303.capone.recommenders.stubs;

import java.util.HashMap;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Speech;

/**
 * Vertex that details what proportion of a certain MP's speeches' headers are certain words.
 */
public class MPVertex
{
	private double[] aVertex;

	/**
	 * @param pMPKey the MP key of the specific MP for whom we wish to create this vertex.
	 * @param pIndex a hashmap that relates each word to its corresponding index in the MPVertex.
	 */
	public MPVertex(String pMPKey, HashMap<String, Integer> pIndex)
	{
		aVertex = new double[pIndex.size()];

		int wordCount = 0;
		MP mp = Capone.getInstance().getParliament().getMP(pMPKey);
		StringBuffer combinedHeaders;
		for (int i = 0; i < aVertex.length; i++)
		{
			aVertex[i] = 0;
		}
		for (Speech sp : mp.returnSpeeches())
		{
			combinedHeaders = new StringBuffer();
			combinedHeaders.append(sp.getHeader1());
			combinedHeaders.append(sp.getHeader2()); // combine the headers together
			for (String st : combinedHeaders.toString().split(" "))
			{
				wordCount++;
				aVertex[pIndex.get(st)]++; // and index each words in the headers into the mpVertex
			}
		}
		divideByInt(wordCount); // divide by the total wordcount, in order to get the proportion of total headers
								// wordcount that use each word.
	}

	/**
	 * @param pVertex the array to store inside MPVertex.
	 */
	public MPVertex(double[] pVertex)
	{
		aVertex = pVertex;
	}

	/**
	 * @return the array representing this MPVertex.
	 */
	public double[] getVertex()
	{
		return aVertex;
	}



	/**
	 * Subtracts the current vector from the given vector.
	 * @param pVertex2 the vector to subtract from the current one.
	 * @return a vector representing the subtraction between the current and given vectors.
	 */
	public MPVertex subtract(MPVertex pVertex2)
	{
		assert aVertex.length == pVertex2.getVertex().length : "unequal vertices!";
		double[] vertexResult = new double[aVertex.length];
		for (int i = 0; i < vertexResult.length; i++)
		{
			vertexResult[i] = aVertex[i] - pVertex2.getVertex()[i];
		}
		return new MPVertex(vertexResult);
	}

	/**
	 * @return the "size" of the current vector, by square rooting the sum of the squares of each of its components.
	 */
	public double vLength()
	{
		double total = 0;
		for (double i : aVertex)
		{
			total = total + i * i;
		}
		return Math.sqrt(total);
	}
	
	//divides each number in the current vector by pVal.
	private void divideByInt(int pVal)
	{
		for (int i = 0; i < aVertex.length; i++)
		{
			aVertex[i] = aVertex[i] / pVal;
		}
	}

}
