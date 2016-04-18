/*******************************************************************************
 *    Copyright 2015, 2016 Taylor G Smith
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.clust4j.algo.pipeline;

import org.apache.commons.math3.linear.AbstractRealMatrix;

import com.clust4j.algo.AbstractClusterer;
import com.clust4j.algo.UnsupervisedClassifier;
import com.clust4j.algo.UnsupervisedClassifierParameters;
import com.clust4j.algo.preprocess.PreProcessor;
import com.clust4j.except.ModelNotFitException;

public class UnsupervisedPipeline<M extends AbstractClusterer & UnsupervisedClassifier> 
		extends Pipeline<UnsupervisedClassifierParameters<M>> 
		implements UnsupervisedClassifier {
	
	private static final long serialVersionUID = 8790601917700667359L;
	protected M fit_model = null;

	public UnsupervisedPipeline(final UnsupervisedClassifierParameters<M> planner, final PreProcessor... pipe) {
		super(planner, pipe);
	}

	public M fit(final AbstractRealMatrix data) {
		synchronized(fitLock) {
			AbstractRealMatrix copy = pipelineTransform(data);
	
			// Build/fit the model
			return fit_model = planner.fitNewModel(copy);
		}
	}
	
	private void ensureModelFit() {
		if(null == fit_model) {
			throw new ModelNotFitException("model has not yet been fit");
		} else { // this is just to avoid the missed branch for coverage
			return;
		}
	}

	@Override
	public int[] getLabels() {
		ensureModelFit();
		return fit_model.getLabels();
	}

	@Override
	public double indexAffinityScore(int[] labels) {
		ensureModelFit();
		return fit_model.indexAffinityScore(labels);
	}

	@Override
	public double silhouetteScore() {
		ensureModelFit();
		return fit_model.silhouetteScore();
	}

	/**
	 * Given an incoming dataframe, pipeline transform and
	 * predict via the fit model
	 * @param newData
	 */
	@Override
	public int[] predict(AbstractRealMatrix newData) {
		ensureModelFit();
		return fit_model.predict(pipelineTransform(newData));
	}
}
