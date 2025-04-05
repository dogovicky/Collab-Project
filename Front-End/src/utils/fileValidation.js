export const FILE_SIZE_LIMIT = 5 * 1024 * 1024; // 5MB
export const ACCEPTED_FILE_TYPES = ['image/jpeg', 'image/png', 'image/jpg', 'image/gif'];

export const validateFileSize = (file) => {
  if (file.size > FILE_SIZE_LIMIT) {
    return `File size must be less than ${FILE_SIZE_LIMIT / (1024 * 1024)}MB`;
  }
  return null;
};

export const validateFileType = (file) => {
  if (!ACCEPTED_FILE_TYPES.includes(file.type)) {
    return `File type must be ${ACCEPTED_FILE_TYPES.map(type => type.split('/')[1]).join(', ')}`;
  }
  return null;
};
