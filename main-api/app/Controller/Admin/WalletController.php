<?php

namespace App\Http\Controllers\Admin;

use App\Domain\Model\User;
use App\Domain\Repositories\WalletRepository;
use App\Http\Controllers\Api\BaseController;

/**
 * Class WalletController
 * @package App\Http\Controllers\Admin
 */
class WalletController extends BaseController
{
    /**
     * @var WalletRepository
     */
    private $repository;

    /**
     * WalletController constructor.
     *
     * @param WalletRepository $repository
     */
    public function __construct(WalletRepository $repository)
    {
        $this->repository = $repository;
    }

    /**
     * @param $userId
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function listByUser($userId)
    {
        if (!$user = User::find($userId)) {
            $this->response->errorNotFound('User not found');
        }

        return $this->success($this->repository->findByUserId($userId)->get());
    }
}