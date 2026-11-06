import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductFormComponent } from './product-form.component';
import { createProductForm } from '../../../utils/product-form.utils';
import { MOCK_CATEGORIES } from '../../../../../core/mocks/data/products.mock';
import { MOCK_SUPPLIERS } from '../../../../../core/mocks/data/suppliers.mock';
import { ValidationMessages } from '../../../../../core/types/providers/validation-messages';
import { DefaultValidationMessages } from '../../../../../core/config/constants/validation.constants';

describe('ProductFormComponent', () => {
    let fixture: ComponentFixture<ProductFormComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ProductFormComponent],
            providers: [{ provide: ValidationMessages, useValue: DefaultValidationMessages }]
        });

        fixture = TestBed.createComponent(ProductFormComponent);
        fixture.componentRef.setInput('form', createProductForm());
        fixture.componentRef.setInput('categories', MOCK_CATEGORIES);
        fixture.componentRef.setInput('suppliers', MOCK_SUPPLIERS);
        fixture.detectChanges();
    });

    it('renders supplier options', () => {
        const options = fixture.nativeElement.querySelectorAll('#supplierId option');

        expect(
            Array.from(options).map(
                option => (option as HTMLOptionElement).textContent?.trim() ?? ''
            )
        ).toEqual(['Select a supplier', ...MOCK_SUPPLIERS.map(supplier => supplier.name)]);
    });

    it('shows the supplier required error when touched without a value', () => {
        const form = createProductForm();
        const supplierControl = form.controls.supplierId;
        supplierControl.markAsTouched();
        fixture.componentRef.setInput('form', form);
        fixture.detectChanges();

        expect(fixture.nativeElement.textContent).toContain('This field is required');
    });
});
