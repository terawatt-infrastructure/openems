import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractModal } from 'src/app/shared/genericComponents/modal/abstractModal';
import { TimeOfUseTariffUtils } from 'src/app/shared/service/utils';
import { ChannelAddress, Currency, CurrentData } from 'src/app/shared/shared';

@Component({
    templateUrl: './modal.html',
})
export class ModalComponent extends AbstractModal {

    protected readonly CONVERT_TIME_OF_USE_TARIFF_STATE = this.Utils.CONVERT_TIME_OF_USE_TARIFF_STATE(this.translate);
    protected priceWithCurrency: any;
    protected controlMode = TimeOfUseTariffUtils.ControlMode;

    protected override getFormGroup(): FormGroup {
        return this.formBuilder.group({
            mode: new FormControl(this.component.properties.mode),
            controlMode: new FormControl(this.component.properties.controlMode),
            chargeConsumptionIsActive: new FormControl(this.component.properties.controlMode === this.controlMode.CHARGE_CONSUMPTION ? true : false),
        });
    }

    protected override getChannelAddresses(): ChannelAddress[] {
        return [
            new ChannelAddress(this.component.id, 'QuarterlyPrices'),
        ];
    }

    protected override onIsInitialized(): void {
        this.subscription.add(
            this.formGroup?.get('chargeConsumptionIsActive')
                .valueChanges
                .subscribe(isActive => {
                    const controlMode: TimeOfUseTariffUtils.ControlMode = isActive ? this.controlMode.CHARGE_CONSUMPTION : this.controlMode.DELAY_DISCHARGE;
                    this.formGroup.controls['controlMode'].setValue(controlMode);
                    this.formGroup.controls['controlMode'].markAsDirty();
                }));
    }

    protected override onCurrentData(currentData: CurrentData): void {
        var quarterlyPrice = currentData.allComponents[this.component.id + '/QuarterlyPrices'];
        var currencyLabel: string = Currency.getCurrencyLabelByEdgeId(this.edge?.id);
        this.priceWithCurrency = this.Utils.CONVERT_PRICE_TO_CENT_PER_KWH(2, currencyLabel)(quarterlyPrice);
    }
}
