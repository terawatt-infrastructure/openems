package io.openems.edge.core.appmanager.validator;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.openems.common.session.Language;
import io.openems.edge.core.appmanager.AppManager;
import io.openems.edge.core.appmanager.AppManagerImpl;
import io.openems.edge.core.appmanager.OpenemsApp;
import io.openems.edge.core.appmanager.OpenemsAppCardinality;
import io.openems.edge.core.appmanager.OpenemsAppCategory;
import io.openems.edge.core.appmanager.OpenemsAppInstance;

@Component(name = CheckCardinality.COMPONENT_NAME)
public class CheckCardinality extends AbstractCheckable implements Checkable {

	public static final String COMPONENT_NAME = "Validator.Checkable.CheckCardinality";

	private final AppManager appManager;
	private OpenemsApp openemsApp;

	private ErrorType errorType = ErrorType.NONE;
	private String errorMessage;
	private OpenemsAppCategory matchingCategory;

	private static enum ErrorType {
		SAME_CATEGORIE, //
		SAME_APP, //
		NONE, //
		OTHER, //
		;
	}

	@Activate
	public CheckCardinality(@Reference AppManager appManager, ComponentContext componentContext) {
		super(componentContext);
		this.appManager = appManager;
	}

	@Override
	public void setProperties(Map<String, ?> properties) {
		this.openemsApp = (OpenemsApp) properties.get("openemsApp");
	}

	@Override
	public boolean check() {
		this.errorType = ErrorType.NONE;
		this.errorMessage = null;
		if (this.appManager == null) {
			this.errorMessage = "App Manager not available!";
			this.errorType = ErrorType.OTHER;
			return false;
		}
		if (!(this.appManager instanceof AppManagerImpl)) {
			this.errorMessage = "Wrong AppManager active!";
			this.errorType = ErrorType.OTHER;
			return false;
		}
		var appManagerImpl = (AppManagerImpl) this.appManager;
		var instantiatedApps = appManagerImpl.getInstantiatedApps();

		switch (this.openemsApp.getCardinality()) {
		case SINGLE:
			if (instantiatedApps.stream().anyMatch(t -> t.appId.equals(this.openemsApp.getAppId()))) {
				// only create one instance of this app
				this.errorType = ErrorType.SAME_APP;
			}
			break;
		case SINGLE_IN_CATEGORY:
			var matchedCategorie = this.getMatchingCategorie(appManagerImpl, instantiatedApps);
			if (matchedCategorie != null) {
				// only create one instance with the same category of this app
				this.matchingCategory = matchedCategorie;
				this.errorType = ErrorType.SAME_CATEGORIE;
			}
			break;
		case MULTIPLE:
			// any number of this app can be instantiated
			break;
		}

		return this.errorType == ErrorType.NONE;
	}

	private OpenemsAppCategory getMatchingCategorie(AppManagerImpl appManager,
			List<OpenemsAppInstance> instantiatedApps) {
		for (var openemsAppInstance : instantiatedApps) {
			try {
				var app = appManager.findAppById(openemsAppInstance.appId);
				if (app.getCardinality() != OpenemsAppCardinality.SINGLE_IN_CATEGORY) {
					continue;
				}
				for (var cat : app.getCategorys()) {
					for (var catOther : this.openemsApp.getCategorys()) {
						if (cat == catOther) {
							return cat;
						}
					}
				}
			} catch (NoSuchElementException e) {
				// if app instance is reworked there may be no app for the instance
				continue;
			}
		}
		return null;
	}

	@Override
	public String getErrorMessage(Language language) {
		switch (this.errorType) {
		case SAME_APP:
			return AbstractCheckable.getTranslation(language, "Validator.Checkable.CheckCardinality.Message.Single",
					this.openemsApp.getAppId());
		case SAME_CATEGORIE:
			return AbstractCheckable.getTranslation(language,
					"Validator.Checkable.CheckCardinality.Message.SingleInCategorie",
					this.matchingCategory.getReadableName(language));
		case OTHER:
			return this.errorMessage;
		case NONE:
			return null;
		}
		return null;
	}

}
