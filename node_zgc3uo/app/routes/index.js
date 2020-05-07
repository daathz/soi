'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
const express = require("express");
const movie_service_1 = require("../services/movie_service");
const router = express.Router();
router.use('/movies', movie_service_1.default);
router.get('/', (req, res, next) => {
    res.render('index', { title: 'Express' });
});
exports.default = router;
//# sourceMappingURL=index.js.map