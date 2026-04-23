import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cashOperationService } from '../../services/cashOperationService';
import { useAuth } from '../../context/AuthContext';
import axiosInstance from '../../api/axios';

const CashOperationForm = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [formData, setFormData] = useState({
    categoryId: '',
    type: 'ENTREE',
    montant: '',
    description: '',
  });
  const [categories, setCategories] = useState([]);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      // ✅ CORRIGÉ : /v1/categories
      const response = await axiosInstance.get('/v1/categories');
      setCategories(response.data || []);
    } catch (error) {
      console.error('Erreur chargement catégories:', error);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.categoryId) newErrors.categoryId = 'La catégorie est obligatoire';
    if (!formData.montant || parseFloat(formData.montant) <= 0) newErrors.montant = 'Montant invalide';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      setSaving(true);
      await cashOperationService.create({
        ...formData,
        categoryId: parseInt(formData.categoryId),
        montant: parseFloat(formData.montant),
        userId: user?.id,
      });
      alert('Opération créée avec succès');
      navigate('/cash-operations');
    } catch (error) {
      console.error('Erreur:', error);
      alert(error.response?.data?.message || 'Erreur lors de la création');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Nouvelle opération de caisse</h1>
        <button onClick={() => navigate('/cash-operations')} className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg">
          ← Retour
        </button>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3">Type d'opération</label>
            <div className="flex gap-4">
              <label className="flex items-center">
                <input type="radio" name="type" value="ENTREE" checked={formData.type === 'ENTREE'} onChange={handleChange} className="mr-2" />
                <span className="text-green-600">Entrée d'argent</span>
              </label>
              <label className="flex items-center">
                <input type="radio" name="type" value="SORTIE" checked={formData.type === 'SORTIE'} onChange={handleChange} className="mr-2" />
                <span className="text-red-600">Sortie d'argent</span>
              </label>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Catégorie <span className="text-red-500">*</span></label>
            <select
              name="categoryId"
              value={formData.categoryId}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded-lg ${errors.categoryId ? 'border-red-500' : 'border-gray-300'}`}
            >
              <option value="">Sélectionner une catégorie</option>
              {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
            {errors.categoryId && <p className="mt-1 text-sm text-red-600">{errors.categoryId}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Montant (FG) <span className="text-red-500">*</span></label>
            <input
              name="montant"
              type="number"
              min="0"
              value={formData.montant}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded-lg ${errors.montant ? 'border-red-500' : 'border-gray-300'}`}
              placeholder="Ex: 50000"
            />
            {errors.montant && <p className="mt-1 text-sm text-red-600">{errors.montant}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              placeholder="Détails de l'opération..."
            />
          </div>

          <div className={`p-4 rounded-lg ${formData.type === 'ENTREE' ? 'bg-green-50' : 'bg-red-50'}`}>
            <p className="font-medium">{formData.type === 'ENTREE' ? '📈 Entrée' : '📉 Sortie'} prévue</p>
            <p className={`text-2xl font-bold ${formData.type === 'ENTREE' ? 'text-green-600' : 'text-red-600'}`}>
              {formData.type === 'ENTREE' ? '+' : '-'} {formData.montant || 0} FG
            </p>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t">
            <button type="button" onClick={() => navigate('/cash-operations')} className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg">
              Annuler
            </button>
            <button type="submit" disabled={saving} className={`px-4 py-2 text-white rounded-lg disabled:opacity-50 ${
              formData.type === 'ENTREE' ? 'bg-green-600 hover:bg-green-700' : 'bg-red-600 hover:bg-red-700'
            }`}>
              {saving ? 'Enregistrement...' : 'Enregistrer'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CashOperationForm;