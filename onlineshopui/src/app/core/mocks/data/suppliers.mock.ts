import { SupplierDto } from '../../types/dtos/supplier.dto';

export const MOCK_SUPPLIERS: SupplierDto[] = [
    {
        id: 'sup-legacy',
        name: 'Legacy Supplier',
        location: 'Unknown',
        description: 'Placeholder supplier assigned to products that existed before supplier tracking.',
        contactEmail: '',
        phone: ''
    },
    {
        id: 'sup-1',
        name: 'TechSource Electronics',
        location: 'Shenzhen, China',
        description: 'Manufacturer and distributor of consumer electronics.',
        contactEmail: 'contact@techsource.example',
        phone: '+86-755-1234567'
    },
    {
        id: 'sup-2',
        name: 'Northline Apparel Co.',
        location: 'Cluj-Napoca, Romania',
        description: 'Textile and apparel supplier for clothing lines.',
        contactEmail: 'sales@northlineapparel.example',
        phone: '+40-264-112233'
    },
    {
        id: 'sup-3',
        name: 'GreenYard Supplies',
        location: 'Bucharest, Romania',
        description: 'Home and garden equipment supplier.',
        contactEmail: 'info@greenyardsupplies.example',
        phone: '+40-21-9988776'
    }
];
